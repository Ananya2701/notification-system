package com.assessment.notification.repository;

import com.assessment.notification.entity.Notification;
import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificationRepository
        extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    boolean existsByUserIdAndTypeAndMessageAndCreatedAtAfter(
            Long userId, NotificationType type, String message, LocalDateTime after);

    @Query("SELECT n FROM Notification n WHERE "
            + "(:status IS NULL OR n.status = :status) AND "
            + "(:type IS NULL OR n.type = :type)")
    Page<Notification> fetchNotifications(@Param("status") NotificationStatus status,
                                          @Param("type") NotificationType type,
                                          Pageable pageable);
    long countByType(NotificationType type);

    long countByTypeAndStatus(NotificationType type, NotificationStatus status);

    long countByStatus(NotificationStatus status);
}
