package com.eccolimp.cacamba_manager.notification.repository;

import com.eccolimp.cacamba_manager.notification.model.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findTop50ByOrderByCreatedAtDesc();
    long countByReadFlagFalse();
    List<Notification> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    @Modifying
    @Query("update Notification n set n.readFlag = true where n.readFlag = false")
    int markAllAsRead();

    @Modifying
    @Query("update Notification n set n.readFlag = true where n.id = :id")
    int markOneAsRead(@Param("id") Long id);
}


