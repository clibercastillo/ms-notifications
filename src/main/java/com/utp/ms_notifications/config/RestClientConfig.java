package com.utp.ms_notifications.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${services.auth.url}")
    private String authUrl;

    @Bean
    public RestClient authRestClient() {
        return RestClient.builder()
                .baseUrl(authUrl)
                .build();
    }
}