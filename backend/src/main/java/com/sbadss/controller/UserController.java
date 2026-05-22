package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.dto.UserResponse;
import com.sbadss.service.UserService;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiEndpoints.USER_BASE)
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("GET /api/v1/users");
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(), CommonMessages.USERS_FETCH_SUCCESS));
    }

    @GetMapping(ApiEndpoints.USER_ID)
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("GET /api/v1/users/{}", id);
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id), CommonMessages.USER_FETCH_SUCCESS));
    }

    @GetMapping(ApiEndpoints.USER_BRANCH)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByBranch(@PathVariable Long branchId) {
        log.info("GET /api/v1/users/branch/{}", branchId);
        return ResponseEntity.ok(ApiResponse.success(userService.getUsersByBranch(branchId), CommonMessages.USERS_FETCH_SUCCESS));
    }

    @PatchMapping(ApiEndpoints.USER_ROLE)
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id, @RequestParam String roleName) {
        log.info("PATCH /api/v1/users/{}/role -> {}", id, roleName);
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserRole(id, roleName), CommonMessages.USER_ROLE_UPDATE_SUCCESS));
    }

    @PatchMapping(ApiEndpoints.USER_STATUS)
    public ResponseEntity<ApiResponse<UserResponse>> toggleUserStatus(@PathVariable Long id) {
        log.info("PATCH /api/v1/users/{}/toggle-status", id);
        return ResponseEntity.ok(ApiResponse.success(userService.toggleUserStatus(id), CommonMessages.USER_STATUS_UPDATE_SUCCESS));
    }

    @PutMapping(ApiEndpoints.USER_ID)
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id, @RequestBody com.sbadss.dto.RegisterRequest request) {
        log.info("PUT /api/v1/users/{}", id);
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(id, request), CommonMessages.USER_UPDATE_SUCCESS));
    }

    @DeleteMapping(ApiEndpoints.USER_ID)
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(CommonMessages.USER_DELETE_SUCCESS));
    }
}
