package com.assessment.notification.dto;

import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String message;
    private NotificationStatus status;
    private LocalDateTime scheduleTime;
    private int retryCount;
    private LocalDateTime lastRetryAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
