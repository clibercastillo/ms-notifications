package com.utp.ms_notifications.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, length = 20)
    private String channel; // EMAIL, PUSH, SMS (simulado por ahora)

    @Builder.Default
    @Column(nullable = false)
    private boolean sent = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}