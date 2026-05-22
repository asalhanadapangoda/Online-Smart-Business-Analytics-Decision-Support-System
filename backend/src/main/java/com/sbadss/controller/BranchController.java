package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.dto.BranchRequest;
import com.sbadss.dto.BranchResponse;
import com.sbadss.service.BranchService;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiEndpoints.BRANCH_BASE)
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getAllBranches() {
        log.info("GET /api/v1/branches");
        return ResponseEntity.ok(ApiResponse.success(branchService.getAllBranches(), CommonMessages.BRANCHES_FETCH_SUCCESS));
    }

    @GetMapping(ApiEndpoints.BRANCH_ID)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(@PathVariable Long id) {
        log.info("GET /api/v1/branches/{}", id);
        return ResponseEntity.ok(ApiResponse.success(branchService.getBranchById(id), CommonMessages.BRANCH_FETCH_SUCCESS));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        log.info("POST /api/v1/branches - name: {}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(branchService.createBranch(request), CommonMessages.BRANCH_CREATE_SUCCESS));
    }

    @PutMapping(ApiEndpoints.BRANCH_ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        log.info("PUT /api/v1/branches/{}", id);
        return ResponseEntity.ok(ApiResponse.success(branchService.updateBranch(id, request), CommonMessages.BRANCH_UPDATE_SUCCESS));
    }

    @DeleteMapping(ApiEndpoints.BRANCH_ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateBranch(@PathVariable Long id) {
        log.info("DELETE /api/v1/branches/{}", id);
        branchService.deactivateBranch(id);
        return ResponseEntity.ok(ApiResponse.success(CommonMessages.BRANCH_DEACTIVATE_SUCCESS));
    }
}
