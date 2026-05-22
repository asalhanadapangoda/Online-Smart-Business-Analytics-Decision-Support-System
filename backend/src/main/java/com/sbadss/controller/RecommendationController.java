package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.service.RecommendationService;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiEndpoints.RECOMMENDATION_BASE)
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping(ApiEndpoints.RECOMMENDATION_CHURN)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getChurnPrediction(
            @PathVariable Long customerId, @RequestParam Long branchId) {
        return ResponseEntity.ok(ApiResponse.success(
                recommendationService.getChurnPrediction(customerId, branchId), CommonMessages.CHURN_PREDICTION_FETCH_SUCCESS));
    }

    @GetMapping(ApiEndpoints.RECOMMENDATION_BUSINESS)
    public ResponseEntity<ApiResponse<List<String>>> getBusinessRecommendations(@RequestParam Long branchId) {
        return ResponseEntity.ok(ApiResponse.success(
                recommendationService.getBusinessRecommendations(branchId), CommonMessages.RECOMMENDATIONS_FETCH_SUCCESS));
    }
}
