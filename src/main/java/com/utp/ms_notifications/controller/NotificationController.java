package com.utp.ms_notifications.controller;

import com.utp.ms_notifications.dto.NotificationResponse;
import com.utp.ms_notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Historial de notificaciones del usuario")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/mine")
    @Operation(summary = "Listar mis notificaciones")
    public ResponseEntity<List<NotificationResponse>> myNotifications() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(notificationService.findMyNotifications(userEmail));
    }
}