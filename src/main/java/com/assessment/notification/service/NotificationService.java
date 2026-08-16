package com.assessment.notification.service;

import com.assessment.notification.dto.NotificationRequestDto;
import com.assessment.notification.dto.NotificationResponseDto;

public interface NotificationService {

    NotificationResponseDto createNotification(NotificationRequestDto requestDto);
}
