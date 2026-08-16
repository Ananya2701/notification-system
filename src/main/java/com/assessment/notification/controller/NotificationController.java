package com.assessment.notification.controller;

import com.assessment.notification.dto.NotificationRequestDto;
import com.assessment.notification.dto.NotificationResponseDto;
import com.assessment.notification.dto.PagedResponse;
import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.entity.NotificationType;
import com.assessment.notification.queue.NotificationQueueService;
import com.assessment.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationQueueService queueService;

    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationRequestDto requestDto) {
        NotificationResponseDto created = notificationService.createNotification(requestDto);
        queueService.enqueue(created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<NotificationResponseDto>> getNotifications(
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<NotificationResponseDto> result =
                notificationService.fetchNotifications(status, type, page, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<NotificationResponseDto> retryNotification(@PathVariable Long id) {
        NotificationResponseDto retried = notificationService.retryNotification(id);
        queueService.enqueue(retried.getId());
        return ResponseEntity.ok(retried);
    }
}
