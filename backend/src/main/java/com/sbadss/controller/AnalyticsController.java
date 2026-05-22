package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.dto.DashboardResponse;
import com.sbadss.service.AnalyticsService;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ApiEndpoints.ANALYTICS_BASE)
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping(ApiEndpoints.ANALYTICS_DASHBOARD)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'CASHIER')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardData(
            @RequestParam(required = false) Long branchId) {
        log.info("REST request to fetch dashboard data for branch: {}", branchId);
        DashboardResponse data = analyticsService.getDashboardData(branchId);
        return ResponseEntity.ok(ApiResponse.success(data, CommonMessages.DASHBOARD_FETCH_SUCCESS));
    }

    @GetMapping(ApiEndpoints.ANALYTICS_PROFIT_LOSS)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<com.sbadss.dto.ProfitLossResponse>> getProfitLossData(
            @RequestParam(required = false) Long branchId) {
        log.info("REST request to fetch P&L data for branch: {}", branchId);
        com.sbadss.dto.ProfitLossResponse data = analyticsService.getProfitLossData(branchId);
        return ResponseEntity.ok(ApiResponse.success(data, CommonMessages.PL_FETCH_SUCCESS));
    }
}
