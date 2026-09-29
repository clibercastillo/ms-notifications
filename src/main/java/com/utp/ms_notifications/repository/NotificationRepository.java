package com.utp.ms_notifications.repository;

import com.utp.ms_notifications.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    // IdDesc como desempate: el broadcast crea muchas filas casi al mismo tiempo
    Page<Notification> findByUserEmailOrderByCreatedAtDescIdDesc(String userEmail, Pageable pageable);

    long countByUserEmailAndReadFalse(String userEmail);

    // El filtro por email evita que alguien marque notificaciones ajenas
    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.read = true where n.id = :id and n.userEmail = :email")
    int markAsRead(@Param("id") Long id, @Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.read = true where n.userEmail = :email and n.read = false")
    int markAllAsRead(@Param("email") String email);
}