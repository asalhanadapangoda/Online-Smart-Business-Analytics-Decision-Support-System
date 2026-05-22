package com.sbadss.util;

public final class CommonMessages {

    private CommonMessages() {} // Prevent instantiation

    // ===================== AUTH =====================
    public static final String REGISTRATION_SUCCESS = "User registered successfully";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";

    // ===================== USER =====================
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_FOUND_ID = "User not found with id: ";
    public static final String USER_FETCH_SUCCESS = "User fetched successfully";
    public static final String USERS_FETCH_SUCCESS = "Users fetched successfully";
    public static final String USER_ROLE_UPDATE_SUCCESS = "User role updated successfully";
    public static final String USER_STATUS_UPDATE_SUCCESS = "User status updated successfully";
    public static final String USER_UPDATE_SUCCESS = "User updated successfully";
    public static final String USER_DELETE_SUCCESS = "User deleted successfully";
    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String ROLE_NOT_FOUND_NAME = "Role not found: ";

    // ===================== BRANCH =====================
    public static final String BRANCH_NOT_FOUND = "Branch not found";
    public static final String BRANCH_NOT_FOUND_ID = "Branch not found with id: ";
    public static final String BRANCH_ALREADY_EXISTS = "Branch with name already exists";
    public static final String BRANCH_FETCH_SUCCESS = "Branch fetched successfully";
    public static final String BRANCHES_FETCH_SUCCESS = "Branches fetched successfully";
    public static final String BRANCH_CREATE_SUCCESS = "Branch created successfully";
    public static final String BRANCH_UPDATE_SUCCESS = "Branch updated successfully";
    public static final String BRANCH_DEACTIVATE_SUCCESS = "Branch deactivated successfully";

    // ===================== CATEGORY =====================
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String CATEGORIES_FETCH_SUCCESS = "Categories fetched successfully";
    public static final String CATEGORY_CREATE_SUCCESS = "Category created successfully";

    // ===================== CHATBOT =====================
    public static final String CHATBOT_QUERY_SUCCESS = "Query processed successfully";

    // ===================== CUSTOMER =====================
    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    public static final String CUSTOMER_NOT_FOUND_ID = "Customer not found: ";
    public static final String CUSTOMER_FOUND = "Customer found";
    public static final String CUSTOMERS_FETCH_SUCCESS = "Customers fetched successfully";
    public static final String CUSTOMER_CREATE_SUCCESS = "Customer created successfully";
    public static final String INSUFFICIENT_LOYALTY_POINTS = "Insufficient loyalty points. Have: ";

    // ===================== EXPENSE CATEGORY =====================
    public static final String EXPENSE_CATEGORY_NOT_FOUND = "Expense category not found";
    public static final String EXPENSE_CATEGORIES_FETCH_SUCCESS = "Expense categories fetched successfully";
    public static final String EXPENSE_CATEGORY_CREATE_SUCCESS = "Expense category created successfully";

    // ===================== EXPENSE =====================
    public static final String EXPENSES_FETCH_SUCCESS = "Expenses fetched successfully";
    public static final String EXPENSE_RECORD_SUCCESS = "Expense recorded successfully";

    // ===================== FORECAST =====================
    public static final String FORECAST_GENERATE_SUCCESS = "Forecast generated successfully";
    public static final String NULL_AI_RESPONSE = "Null response from AI service";

    // ===================== NOTIFICATION =====================
    public static final String NOTIFICATION_READ_SUCCESS = "Marked as read";
    public static final String NOTIFICATION_READ_ALL_SUCCESS = "All marked as read";
    public static final String NOTIFICATIONS_FETCH_SUCCESS = "Notifications fetched";
    public static final String NOTIFICATION_COUNT_FETCH_SUCCESS = "Count fetched";

    // ===================== PRODUCT =====================
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String PRODUCTS_FETCH_SUCCESS = "Products fetched successfully";
    public static final String PRODUCT_CREATE_SUCCESS = "Product created successfully";
    public static final String STOCK_UPDATE_SUCCESS = "Stock updated successfully";
    public static final String PRODUCTS_IMPORT_SUCCESS = "Products imported successfully";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock for product: ";
    public static final String CSV_IMPORT_FAILED = "CSV Import failed: ";

    // ===================== RECOMMENDATION =====================
    public static final String CHURN_PREDICTION_FETCH_SUCCESS = "Churn prediction fetched";
    public static final String RECOMMENDATIONS_FETCH_SUCCESS = "Recommendations fetched";

    // ===================== REPORT =====================
    public static final String REPORT_NOT_FOUND = "Report not found: ";
    public static final String REPORT_GENERATION_FAILED = "Report generation failed: ";
    public static final String REPORT_NOT_AVAILABLE = "Report is not yet available for download";
    public static final String REPORT_FILE_NOT_FOUND = "Report file not found on server";
    public static final String REPORT_HISTORY_FETCH_SUCCESS = "Report history fetched";

    // ===================== SALE =====================
    public static final String SALE_NOT_FOUND = "Sale not found";
    public static final String SALES_FETCH_SUCCESS = "Sales fetched successfully";
    public static final String SALE_RECORD_SUCCESS = "Sale recorded successfully";
    public static final String BILL_COMPLETE_SUCCESS = "Bill completed successfully";
    public static final String SALE_DELETE_SUCCESS = "Sale deleted successfully";
    public static final String CASHIER_DELETE_RESTRICTION = "Cashiers cannot delete a completed sale.";

    // ===================== ANALYTICS =====================
    public static final String DASHBOARD_FETCH_SUCCESS = "Dashboard data fetched successfully";
    public static final String PL_FETCH_SUCCESS = "P&L data fetched successfully";

    // ===================== SYSTEM =====================
    public static final String PDF_GENERATION_ERROR = "Error generating PDF";
}
