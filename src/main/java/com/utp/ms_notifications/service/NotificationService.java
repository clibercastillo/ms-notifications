package com.utp.ms_notifications.service;

import com.utp.ms_notifications.dto.BookingEvent;
import com.utp.ms_notifications.dto.NotificationResponse;
import com.utp.ms_notifications.entity.Notification;
import com.utp.ms_notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void processBookingEvent(BookingEvent event) {
        String message = buildMessage(event);

        Notification notification = Notification.builder()
                .bookingId(event.getBookingId())
                .userEmail(event.getUserEmail())
                .message(message)
                .channel("EMAIL")
                .sent(true)
                .build();

        notificationRepository.save(notification);

        // Simulación de envío real (aquí luego conectas SendGrid, SMTP, etc.)
        log.info("📧 Notificación enviada a {}: {}", event.getUserEmail(), message);
    }

    public List<NotificationResponse> findMyNotifications(String userEmail) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(n -> new NotificationResponse(n.getId(), n.getBookingId(), n.getMessage(), n.getChannel(), n.getCreatedAt()))
                .collect(Collectors.toList());
    }

    private String buildMessage(BookingEvent event) {
        return switch (event.getStatus()) {
            case "PENDING" -> "Tu reserva #" + event.getBookingId() + " fue creada, pendiente de confirmación.";
            case "CONFIRMED" -> "Tu reserva #" + event.getBookingId() + " fue confirmada para el " + event.getBookingDate() + " a las " + event.getStartTime() + ".";
            case "CANCELLED" -> "Tu reserva #" + event.getBookingId() + " fue cancelada.";
            case "COMPLETED" -> "Tu reserva #" + event.getBookingId() + " fue completada. ¡Gracias por jugar!";
            default -> "Actualización de tu reserva #" + event.getBookingId();
        };
    }
}