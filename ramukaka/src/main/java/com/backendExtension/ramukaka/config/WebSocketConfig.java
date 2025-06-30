package com.backendExtension.ramukaka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/signal").setAllowedOrigins("http://localhost:3000", "http://localhost:8080", "vscode-webview://0gaicjj2tfmd06gl35r8ggu10ud1309mqtnll7k5s9e5ovaum6a3","vscode-webview://0f4kanm96rmq64u1137cdihnkmi8kud4svt81t27qupo40q02ner").withSockJS();

    }
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic");
    }
}