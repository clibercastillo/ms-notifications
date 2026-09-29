package com.utp.ms_notifications.controller;

import com.utp.ms_notifications.dto.NotificationPageResponse;
import com.utp.ms_notifications.dto.NotificationResponse;
import com.utp.ms_notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Historial de notificaciones del usuario")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/mine")
    @Operation(summary = "Listar mis notificaciones (todas)")
    public ResponseEntity<List<NotificationResponse>> myNotifications() {
        return ResponseEntity.ok(notificationService.findMyNotifications(currentUser()));
    }

    @GetMapping("/page")
    @Operation(summary = "Listar mis notificaciones paginadas")
    public ResponseEntity<NotificationPageResponse> myNotificationsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(notificationService.findMyNotificationsPage(currentUser(), page, size));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Cantidad de notificaciones no leídas")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(currentUser())));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar una notificación como leída")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id, currentUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Marcar todas mis notificaciones como leídas")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead(currentUser());
        return ResponseEntity.noContent().build();
    }

    private String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}