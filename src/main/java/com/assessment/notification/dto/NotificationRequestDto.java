package com.assessment.notification.dto;

import com.assessment.notification.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationRequestDto {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "type is required")
    private NotificationType type;

    @NotBlank(message = "message is required")
    private String message;

    @NotNull(message = "scheduleTime is required")
    private LocalDateTime scheduleTime;
}
