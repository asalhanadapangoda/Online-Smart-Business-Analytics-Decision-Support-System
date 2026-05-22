package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.entity.Notification;
import com.sbadss.entity.User;
import com.sbadss.service.NotificationService;
import com.sbadss.util.ApiEndpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.NOTIFICATION_BASE)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyNotifications(user.getId()), "Notifications fetched"));
    }

    @GetMapping(ApiEndpoints.NOTIFICATION_UNREAD_COUNT)
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(user.getId()), "Count fetched"));
    }

    @PatchMapping(ApiEndpoints.NOTIFICATION_READ)
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read"));
    }

    @PatchMapping(ApiEndpoints.NOTIFICATION_READ_ALL)
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal(expression = "user") User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All marked as read"));
    }
}
