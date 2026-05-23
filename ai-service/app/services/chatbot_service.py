import os
import re
import uuid
import logging
import requests
from typing import List, Optional

logger = logging.getLogger(__name__)

BACKEND_URL = os.getenv("SPRING_BOOT_API_URL", "http://localhost:8080")

# ─── NLP Intent Patterns ─────────────────────────────────────────────────────
INTENT_PATTERNS = {
    "REVENUE_QUERY":   [r"revenue", r"income", r"earnings", r"sales total", r"how much.*make"],
    "PROFIT_QUERY":    [r"profit", r"net profit", r"margin", r"gain"],
    "EXPENSE_QUERY":   [r"expense", r"cost", r"spending", r"expenditure"],
    "TOP_PRODUCTS":    [r"top product", r"best sell", r"most popular", r"which product"],
    "GROWTH_QUERY":    [r"growth", r"trend", r"increase", r"decrease", r"compar"],
    "CUSTOMER_QUERY":  [r"customer", r"client", r"buyer", r"active customer"],
    "RECOMMENDATION":  [r"recommend", r"suggest", r"improve", r"strategy", r"how can", r"what should"],
    "FORECAST_QUERY":  [r"forecast", r"predict", r"future", r"next month", r"next week"],
}

RESPONSES = {
    "REVENUE_QUERY":   "Your revenue data has been retrieved from the analytics engine. Check the KPI cards on your dashboard for real-time figures.",
    "PROFIT_QUERY":    "Net Profit = Total Revenue − Total Expenses. Your current profit metrics are shown on the KPI dashboard.",
    "EXPENSE_QUERY":   "Your expense breakdown by category is visualized in the Expense Overview pie chart on your dashboard.",
    "TOP_PRODUCTS":    "The Top Products bar chart on your dashboard shows the best-selling items by quantity and revenue.",
    "GROWTH_QUERY":    "Growth is calculated as: ((Current Period − Previous Period) / Previous Period) × 100. Check the Sales Trends chart.",
    "CUSTOMER_QUERY":  "Your active customer count and segmentation are available in the Customer Analytics section.",
    "RECOMMENDATION":  "📊 Recommendations to improve profitability:\n1. Focus marketing on your top 3 products\n2. Reduce costs in the highest expense category\n3. Target high-value customers with loyalty programs\n4. Analyze slow-moving inventory and run clearance",
    "FORECAST_QUERY":  "Use the AI Forecasting module to get a 7-90 day revenue prediction for your branch.",
    "UNKNOWN":         "I can help with revenue, profit, expenses, top products, growth trends, customers, and business recommendations. What would you like to know?",
}

SUGGESTED_PROMPTS = [
    "Show me this month's revenue",
    "What is our net profit?",
    "Which product is selling the most?",
    "Suggest strategies to improve sales",
    "Forecast next 30 days revenue",
    "Show customer analytics",
]


def process_message(
    message: str,
    session_id: Optional[str] = None,
    branch_id: Optional[int] = None,
    auth_token: Optional[str] = None
) -> dict:
    """
    NLP pipeline:
    1. Tokenize and normalize input
    2. Detect intent via regex pattern matching
    3. Calculate confidence
    4. Call Spring Boot backend for dynamic analytics
    5. Return structured response
    """
    if not session_id:
        session_id = str(uuid.uuid4())

    normalized = message.lower().strip()
    logger.info(f"Processing message: '{normalized[:50]}...' for session: {session_id}")

    # Intent Detection
    detected_intent, confidence = _detect_intent(normalized)
    logger.info(f"Intent: {detected_intent}, Confidence: {confidence:.2f}")

    response_message = RESPONSES.get(detected_intent, RESPONSES["UNKNOWN"])
    data = None
    
    headers = {}
    if auth_token:
        headers["Authorization"] = auth_token

    try:
        if detected_intent in ["REVENUE_QUERY", "EXPENSE_QUERY", "TOP_PRODUCTS", "GROWTH_QUERY", "RECOMMENDATION"]:
            url = f"{BACKEND_URL}/api/analytics/dashboard"
            params = {"branchId": branch_id} if branch_id else {}
            res = requests.get(url, headers=headers, params=params, timeout=5)
            if res.status_code == 200:
                data = res.json()
                if detected_intent == "REVENUE_QUERY":
                    rev = data.get('totalRevenue', 0)
                    response_message = f"Your total revenue is ${rev:,.2f}."
                elif detected_intent == "EXPENSE_QUERY":
                    exp = data.get('totalExpenses', 0)
                    response_message = f"Your total expenses are ${exp:,.2f}."
                elif detected_intent == "TOP_PRODUCTS":
                    products = data.get('topSellingProducts', [])
                    if products:
                        top = ", ".join([f"{p.get('productName', 'Unknown')} ({p.get('quantitySold', 0)} sold)" for p in products[:3]])
                        response_message = f"Your top selling products are: {top}."
                elif detected_intent == "GROWTH_QUERY":
                    rev_grow = data.get('revenueGrowth', 0)
                    response_message = f"Your revenue growth is {rev_grow}% compared to the previous period."
                elif detected_intent == "RECOMMENDATION":
                    rev_grow = data.get('revenueGrowth', 0)
                    if rev_grow < 0:
                        response_message = "Your revenue is declining. Consider running a marketing campaign on top products."
                    else:
                        response_message = "Your revenue is growing! Keep focusing on your high-performing items."
                        
        elif detected_intent == "PROFIT_QUERY":
            url = f"{BACKEND_URL}/api/analytics/profit-loss"
            params = {"branchId": branch_id} if branch_id else {}
            res = requests.get(url, headers=headers, params=params, timeout=5)
            if res.status_code == 200:
                data = res.json()
                net = data.get('netProfit', 0)
                gross = data.get('grossRevenue', 0)
                margin = (net / gross * 100) if gross > 0 else 0
                response_message = f"Your net profit is ${net:,.2f} with a profit margin of {margin:.1f}%."

        elif detected_intent == "CUSTOMER_QUERY":
            url = f"{BACKEND_URL}/api/customers"
            res = requests.get(url, headers=headers, timeout=5)
            if res.status_code == 200:
                customers = res.json()
                # handle both pageable and list response
                if isinstance(customers, dict) and 'content' in customers:
                    customers_list = customers['content']
                elif isinstance(customers, list):
                    customers_list = customers
                else:
                    customers_list = []
                data = {"totalCustomers": len(customers_list)}
                response_message = f"You have {len(customers_list)} active customers."

    except Exception as e:
        logger.error(f"Error calling backend: {e}")
        response_message += "\n(Note: Live data is currently unavailable.)"

    suggestions = [p for p in SUGGESTED_PROMPTS if p.lower() not in normalized][:3]

    return {
        "session_id": session_id,
        "intent": detected_intent,
        "confidence": confidence,
        "message": response_message,
        "data": data,
        "suggested_prompts": suggestions
    }


def _detect_intent(message: str) -> tuple[str, float]:
    best_intent = "UNKNOWN"
    best_score = 0.0

    for intent, patterns in INTENT_PATTERNS.items():
        score = 0
        for pattern in patterns:
            if re.search(pattern, message):
                score += 1.0 / len(patterns)
        if score > best_score:
            best_score = score
            best_intent = intent

    # Minimum threshold
    if best_score < 0.15:
        return "UNKNOWN", 0.0

    # Scale to 0.5 - 0.99
    confidence = min(0.50 + (best_score * 0.49), 0.99)
    return best_intent, round(confidence, 4)
