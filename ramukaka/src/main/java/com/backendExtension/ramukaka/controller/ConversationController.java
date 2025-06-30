package com.backendExtension.ramukaka.controller;

import com.backendExtension.ramukaka.aiservice.AiService;
import com.backendExtension.ramukaka.dto.ConversationRequest;
import com.backendExtension.ramukaka.service.ConversationSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversation")
public class ConversationController {
    private final AiService aiService;
    private final ConversationSessionManager sessionManager;

    @PostMapping
    public Mono<String> converse(@RequestBody ConversationRequest request) {
        String latestCode = sessionManager.getLatestSharedCode(request.getRoom());

        String prompt = """
      You are RAMU kaka 🤖 — a helpful developer assistant.

      👤 User asked: %s

      💾 Workspace files:
      %s

      Please respond concisely and be helpful!
    """.formatted(request.getText(), latestCode);

        return aiService.askGemini(prompt);
    }


}

