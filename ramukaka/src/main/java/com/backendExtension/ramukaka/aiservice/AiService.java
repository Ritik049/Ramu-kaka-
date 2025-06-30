package com.backendExtension.ramukaka.aiservice;

import com.fasterxml.jackson.databind.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(WebClient.Builder builder,
                     @Value("${gemini.api.url}") String baseUrl,
                     @Value("${gemini.api.key}") String apiKey) {
        this.webClient = builder
                .baseUrl(baseUrl + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<String> askGemini(String prompt) {
        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractText)
                .onErrorResume(ex -> {
                    if (ex instanceof org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable) {
                        return Mono.just("❌ Gemini API is temporarily unavailable (503). Please try again later.");
                    } else {
                        return Mono.just("❌ Unexpected error: " + ex.getMessage());
                    }
                });
    }

    private String extractText(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            return root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "❌ Gemini error: " + e.getMessage();
        }
    }
}