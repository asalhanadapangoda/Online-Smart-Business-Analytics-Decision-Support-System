import logging
import numpy as np
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import StandardScaler

logger = logging.getLogger(__name__)

class ChurnModelService:
    def __init__(self):
        self.model = None
        self.scaler = None
        self._initialize_model()

    def _initialize_model(self):
        """
        Generates synthetic customer data and trains a LogisticRegression model.
        Phase 2 implementation for demonstration.
        """
        logger.info("Initializing ML Churn Model and generating synthetic training data...")
        
        # 1. Synthesize Data
        np.random.seed(42)
        n_samples = 1000
        
        # Features: days_since_last_purchase, total_purchases, average_purchase_value
        days_since_last_purchase = np.random.exponential(scale=60, size=n_samples)
        total_purchases = np.random.poisson(lam=10, size=n_samples)
        average_purchase_value = np.random.normal(loc=500, scale=150, size=n_samples)
        
        # Ensure non-negative bounds
        days_since_last_purchase = np.clip(days_since_last_purchase, 0, 365)
        total_purchases = np.clip(total_purchases, 1, 100)
        average_purchase_value = np.clip(average_purchase_value, 10, 5000)
        
        df = pd.DataFrame({
            'days_since_last_purchase': days_since_last_purchase,
            'total_purchases': total_purchases,
            'average_purchase_value': average_purchase_value
        })
        
        # Create labels based on a logical combination with some noise
        # High days + Low purchases + Low value -> Higher Churn Probability
        score = (df['days_since_last_purchase'] / 30.0) * 0.4 \
              - (df['total_purchases'] / 10.0) * 0.3 \
              - (df['average_purchase_value'] / 500.0) * 0.3
        
        # Convert score to probability using sigmoid
        prob = 1 / (1 + np.exp(-score))
        
        # Add some random noise and classify
        noise = np.random.uniform(-0.1, 0.1, n_samples)
        df['churn'] = ((prob + noise) > 0.5).astype(int)
        
        X = df[['days_since_last_purchase', 'total_purchases', 'average_purchase_value']]
        y = df['churn']
        
        # 2. Scale and Train
        self.scaler = StandardScaler()
        X_scaled = self.scaler.fit_transform(X)
        
        self.model = LogisticRegression(class_weight='balanced', random_state=42)
        self.model.fit(X_scaled, y)
        
        logger.info("ML Churn Model training completed successfully.")

    def predict_customer_churn(
        self,
        customer_id: int,
        days_since_last_purchase: int,
        total_purchases: int,
        average_purchase_value: float
    ) -> dict:
        """
        Takes raw customer metrics, scales them, and predicts churn using the trained model.
        """
        # Feature matrix formulation
        X_new = pd.DataFrame({
            'days_since_last_purchase': [days_since_last_purchase],
            'total_purchases': [total_purchases],
            'average_purchase_value': [average_purchase_value]
        })
        
        X_new_scaled = self.scaler.transform(X_new)
        
        # Predict Probability
        churn_probability = float(self.model.predict_proba(X_new_scaled)[0][1])
        
        # Determine risk level & recommendation
        if churn_probability > 0.7:
            risk_level = "HIGH"
            recommendation = "Immediate action required. Offer personalized discount or loyalty rewards."
        elif churn_probability > 0.4:
            risk_level = "MEDIUM"
            recommendation = "Send re-engagement email with product recommendations based on past purchases."
        else:
            risk_level = "LOW"
            recommendation = "Customer is healthy. Continue standard engagement and loyalty programs."
            
        return {
            "customer_id": customer_id,
            "churn_probability": round(churn_probability, 4),
            "risk_level": risk_level,
            "recommendation": recommendation
        }

# Instantiate a singleton service
churn_service_instance = ChurnModelService()
