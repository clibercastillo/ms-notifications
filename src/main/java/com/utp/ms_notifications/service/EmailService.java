package com.utp.ms_notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        log.info("📧 [EMAIL FICTICIO] Para: {} | Asunto: {} | Cuerpo: {}", to, subject, body);
    }
}