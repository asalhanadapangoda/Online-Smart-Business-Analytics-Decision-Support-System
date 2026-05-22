package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.dto.ChatRequest;
import com.sbadss.dto.ChatResponse;
import com.sbadss.entity.User;
import com.sbadss.service.ChatbotService;
import com.sbadss.util.ApiEndpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ApiEndpoints.CHATBOT_BASE)
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping(ApiEndpoints.CHATBOT_QUERY)
    public ResponseEntity<ApiResponse<ChatResponse>> query(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal(expression = "user") User user) {
        log.info("POST /api/v1/chatbot/query - intent processing for user: {}", user.getUsername());
        ChatResponse response = chatbotService.processMessage(request, user.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Query processed successfully"));
    }
}
