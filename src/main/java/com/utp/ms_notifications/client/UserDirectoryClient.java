package com.utp.ms_notifications.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDirectoryClient {

    private final RestClient authRestClient;

    @Value("${internal.api-key}")
    private String internalApiKey;

    public List<String> getAllEmails() {
        try {
            List<String> emails = authRestClient.get()
                    .uri("/api/auth/internal/emails")
                    .header("X-Internal-Key", internalApiKey)
                    .retrieve()
                    .body(List.class);
            return emails != null ? emails : Collections.emptyList();
        } catch (Exception e) {
            log.error("No se pudo obtener la lista de correos desde ms-auth", e);
            return Collections.emptyList();
        }
    }
}