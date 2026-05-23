package com.sbadss.service.impl;

import com.sbadss.dto.ChatRequest;
import com.sbadss.dto.ChatResponse;
import com.sbadss.entity.ChatMessage;
import com.sbadss.entity.User;
import com.sbadss.repository.ChatMessageRepository;
import com.sbadss.repository.UserRepository;
import com.sbadss.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final RestTemplate restTemplate;

    @Value("${sbadss.ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    @Override
    @Transactional
    public ChatResponse processMessage(ChatRequest request, Long userId) {
        log.info("Processing chatbot message from user: {}, session: {}", userId, request.getSessionId());

        String sessionId = request.getSessionId() != null ? 
                request.getSessionId() : UUID.randomUUID().toString();

        // Retrieve Authorization token from context
        String authToken = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest httpServletRequest = attributes.getRequest();
            authToken = httpServletRequest.getHeader("Authorization");
        }

        // Build the request to the AI service
        String url = aiServiceUrl + "/api/v1/chatbot/query";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        if (authToken != null) {
            headers.set("Authorization", authToken);
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", request.getMessage());
        requestBody.put("session_id", sessionId);
        requestBody.put("branch_id", request.getBranchId());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ChatResponse aiResponse;
        try {
            ResponseEntity<ChatResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, ChatResponse.class);
            aiResponse = response.getBody();
        } catch (Exception e) {
            log.error("Failed to call AI Service at {}: {}", url, e.getMessage());
            // Fallback response
            aiResponse = ChatResponse.builder()
                    .sessionId(sessionId)
                    .message("I'm sorry, my analytical engine is currently offline. Please try again later.")
                    .intent("UNKNOWN")
                    .confidence(0.0)
                    .build();
        }

        if (aiResponse == null) {
            aiResponse = ChatResponse.builder()
                    .sessionId(sessionId)
                    .message("Failed to process the request.")
                    .intent("UNKNOWN")
                    .confidence(0.0)
                    .build();
        }

        // Persist conversation
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            ChatMessage chatMsg = ChatMessage.builder()
                    .sessionId(sessionId)
                    .user(user)
                    .userMessage(request.getMessage())
                    .botResponse(aiResponse.getMessage())
                    .intentDetected(aiResponse.getIntent())
                    .confidenceScore(aiResponse.getConfidence())
                    .build();
            chatMessageRepository.save(chatMsg);
        }

        return aiResponse;
    }
}
