package com.history.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Добавляем User-Agent для всех запросов
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            if (!headers.containsKey("User-Agent")) {
                headers.set("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                                "(KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            }
            return execution.execute(request, body);
        });

        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}