package com.sbadss.util;

public final class ApiEndpoints {

    private ApiEndpoints() {} // Prevent instantiation

    // Base Prefixes
    public static final String API_V1 = "/api/v1";
    public static final String API = "/api";

    // ===================== AUTH =====================
    public static final String AUTH_BASE = API + "/auth";
    public static final String AUTH_V1_BASE = API_V1 + "/auth";
    public static final String AUTH_REGISTER = "/register";
    public static final String AUTH_LOGIN = "/login";

    // ===================== ANALYTICS =====================
    public static final String ANALYTICS_BASE = API + "/analytics";
    public static final String ANALYTICS_DASHBOARD = "/dashboard";
    public static final String ANALYTICS_PROFIT_LOSS = "/profit-loss";

    // ===================== BRANCH =====================
    public static final String BRANCH_BASE = API_V1 + "/branches";
    public static final String BRANCH_ID = "/{id}";

    // ===================== CATEGORY =====================
    public static final String CATEGORY_BASE = API + "/categories";

    // ===================== CHATBOT =====================
    public static final String CHATBOT_BASE = API_V1 + "/chatbot";
    public static final String CHATBOT_QUERY = "/query";

    // ===================== CUSTOMER =====================
    public static final String CUSTOMER_BASE = API + "/customers";
    public static final String CUSTOMER_SEARCH = "/search";

    // ===================== EXPENSE CATEGORY =====================
    public static final String EXPENSE_CATEGORY_BASE = API + "/expense-categories";

    // ===================== EXPENSE =====================
    public static final String EXPENSE_BASE = API + "/expenses";

    // ===================== FORECAST =====================
    public static final String FORECAST_BASE = API_V1 + "/forecasts";
    public static final String FORECAST_SALES = "/sales";

    // ===================== NOTIFICATION =====================
    public static final String NOTIFICATION_BASE = API_V1 + "/notifications";
    public static final String NOTIFICATION_UNREAD_COUNT = "/unread-count";
    public static final String NOTIFICATION_READ = "/{id}/read";
    public static final String NOTIFICATION_READ_ALL = "/read-all";

    // ===================== PRODUCT =====================
    public static final String PRODUCT_BASE = API + "/products";
    public static final String PRODUCT_STOCK = "/{id}/stock";
    public static final String PRODUCT_IMPORT = "/import";

    // ===================== RECOMMENDATION =====================
    public static final String RECOMMENDATION_BASE = API_V1 + "/recommendations";
    public static final String RECOMMENDATION_CHURN = "/churn/{customerId}";
    public static final String RECOMMENDATION_BUSINESS = "/business";

    // ===================== REPORT =====================
    public static final String REPORT_BASE = API_V1 + "/reports";
    public static final String REPORT_GENERATE = "/generate";
    public static final String REPORT_DOWNLOAD = "/{id}/download";
    public static final String REPORT_HISTORY = "/history";

    // ===================== SALE =====================
    public static final String SALE_BASE = API + "/sales";
    public static final String SALE_PING = "/ping";
    public static final String SALE_COMPLETE = "/{id}/complete";
    public static final String SALE_INVOICE = "/{id}/invoice";
    public static final String SALE_ID = "/{id}";

    // ===================== USER =====================
    public static final String USER_BASE = API_V1 + "/users";
    public static final String USER_BRANCH = "/branch/{branchId}";
    public static final String USER_ROLE = "/{id}/role";
    public static final String USER_STATUS = "/{id}/toggle-status";
    public static final String USER_ID = "/{id}";
}
