package com.assessment.notification.repository;

import com.assessment.notification.entity.Notification;
import com.assessment.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    boolean existsByUserIdAndTypeAndMessageAndCreatedAtAfter(
            Long userId, NotificationType type, String message, LocalDateTime after);
}
