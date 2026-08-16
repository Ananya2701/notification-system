package com.assessment.notification.service.impl;

import com.assessment.notification.dto.NotificationRequestDto;
import com.assessment.notification.dto.NotificationResponseDto;
import com.assessment.notification.dto.PagedResponse;
import com.assessment.notification.entity.Notification;
import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.entity.NotificationType;
import com.assessment.notification.exception.DuplicateNotificationException;
import com.assessment.notification.exception.InvalidMessageException;
import com.assessment.notification.repository.NotificationRepository;
import com.assessment.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final long BUFFER_TIME = 5;
    private static final int MAX_ALLOWED_REPEATS = 3;

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto requestDto) {

        validate(requestDto.getMessage());

        rejectIfDuplicate(requestDto);

        Notification notification = Notification.builder()
                .userId(requestDto.getUserId())
                .type(requestDto.getType())
                .message(requestDto.getMessage())
                .status(NotificationStatus.PENDING)
                .scheduleTime(requestDto.getScheduleTime())
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification created id={} userId={} type={}", saved.getId(), saved.getUserId(), saved.getType());

        return toResponseDto(saved);
    }

    @Override
    public PagedResponse<NotificationResponseDto> fetchNotifications(
            NotificationStatus status, NotificationType type, int page, int size) {


        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> result = notificationRepository.fetchNotifications(status, type, pageable);

        List<NotificationResponseDto> content = result.getContent().stream()
                .map(this::toResponseDto)
                .toList();

        return PagedResponse.<NotificationResponseDto>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .build();
    }

    private void rejectIfDuplicate(NotificationRequestDto requestDto) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(BUFFER_TIME);
        boolean isDuplicate = notificationRepository.existsByUserIdAndTypeAndMessageAndCreatedAtAfter(
                requestDto.getUserId(), requestDto.getType(), requestDto.getMessage(), fiveMinutesAgo);

        if (isDuplicate) {
            throw new DuplicateNotificationException(
                    "Duplicate notification: The user has already submitted the same type and message content notification within the last " + BUFFER_TIME + " minutes");
        }
    }

    public void validate(String message) {
        if (message == null || message.isBlank()) {
            throw new InvalidMessageException("Message must not be blank");
        }

        Map<String, Integer> wordFrequency = new HashMap<>();
        String[] words = message.trim().split("\\s+");

        for (String rawWord : words) {
            String normalizedWord = rawWord.toLowerCase();
            if (normalizedWord.isEmpty()) {
                continue;
            }
            int count = wordFrequency.merge(normalizedWord, 1, Integer::sum);
            if (count > MAX_ALLOWED_REPEATS) {
                throw new InvalidMessageException("Message rejected. Word " + normalizedWord + " is repeating more than " + MAX_ALLOWED_REPEATS + " times");
            }
        }
    }

    private NotificationResponseDto toResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .scheduleTime(notification.getScheduleTime())
                .retryCount(notification.getRetryCount())
                .lastRetryAt(notification.getLastRetryAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
