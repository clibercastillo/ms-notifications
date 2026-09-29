package com.utp.ms_notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long bookingId;
    private String message;
    private String channel;
    private LocalDateTime createdAt;
    private boolean read;
}