package com.utp.ms_notifications.service;

import com.utp.ms_notifications.client.UserDirectoryClient;
import com.utp.ms_notifications.dto.BookingEvent;
import com.utp.ms_notifications.dto.NotificationResponse;
import com.utp.ms_notifications.entity.Notification;
import com.utp.ms_notifications.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.utp.ms_notifications.dto.NotificationPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final UserDirectoryClient userDirectoryClient;
    private static final int MAX_PAGE_SIZE = 20;

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
        emailService.sendEmail(event.getUserEmail(), "Actualización de tu reserva Walon", message);

        if ("CANCELLED".equals(event.getStatus())) {
            broadcastCancellation(event);
        }
    }

    private void broadcastCancellation(BookingEvent event) {
        String broadcastMessage = "Se liberó un horario: cancha #" + event.getStadiumId()
                + " el " + event.getBookingDate() + " a las " + event.getStartTime() + ".";

        List<String> emails = userDirectoryClient.getAllEmails();
        for (String email : emails) {
            if (email.equalsIgnoreCase(event.getUserEmail())) continue;

            Notification notification = Notification.builder()
                    .bookingId(event.getBookingId())
                    .userEmail(email)
                    .message(broadcastMessage)
                    .channel("EMAIL")
                    .sent(true)
                    .build();
            notificationRepository.save(notification);
            emailService.sendEmail(email, "¡Horario disponible en Walon!", broadcastMessage);
        }
        log.info("Broadcast de cancelación enviado a {} usuarios", emails.size());
    }

    public List<NotificationResponse> findMyNotifications(String userEmail) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationPageResponse findMyNotificationsPage(String userEmail, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

        Page<Notification> result = notificationRepository
                .findByUserEmailOrderByCreatedAtDescIdDesc(userEmail, PageRequest.of(safePage, safeSize));

        List<NotificationResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new NotificationPageResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                notificationRepository.countByUserEmailAndReadFalse(userEmail)
        );
    }

    @Transactional(readOnly = true)
    public long countUnread(String userEmail) {
        return notificationRepository.countByUserEmailAndReadFalse(userEmail);
    }

    @Transactional
    public void markAsRead(Long id, String userEmail) {
        notificationRepository.markAsRead(id, userEmail);
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        notificationRepository.markAllAsRead(userEmail);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getBookingId(), n.getMessage(),
                n.getChannel(), n.getCreatedAt(), n.isRead());
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