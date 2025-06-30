package com.backendExtension.ramukaka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "vscode-webview://0gaicjj2tfmd06gl35r8ggu10ud1309mqtnll7k5s9e5ovaum6a3","vscode-webview://0f4kanm96rmq64u1137cdihnkmi8kud4svt81t27qupo40q02ner") // allow all origins with credentials
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}