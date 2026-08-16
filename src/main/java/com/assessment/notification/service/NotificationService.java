package com.assessment.notification.service;

import com.assessment.notification.dto.NotificationRequestDto;
import com.assessment.notification.dto.NotificationResponseDto;
import com.assessment.notification.dto.PagedResponse;
import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.entity.NotificationType;

public interface NotificationService {

    NotificationResponseDto createNotification(NotificationRequestDto requestDto);

    PagedResponse<NotificationResponseDto> fetchNotifications(
            NotificationStatus status, NotificationType type, int page, int size);
}
