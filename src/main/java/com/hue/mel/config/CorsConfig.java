package com.hue.mel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Apply CORS rules to every single endpoint in the gateway
                        .allowedOriginPatterns("*") // Allows any frontend URL to connect (Mandatory for easy cloud testing)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Authorized HTTP request methods
                        .allowedHeaders("*") // Allows the frontend to send custom headers like 'X-Member-Token'
                        .exposedHeaders("X-User-Id", "X-RateLimit-Violated") // Allows frontend javascript to read these custom tracking context keys
                        .allowCredentials(true) // Supports future secure cookies or session handling
                        .maxAge(3600); // Caches this permission rule for 1 hour so the browser doesn't have to re-ask on every click
            }
        };
    }
}
