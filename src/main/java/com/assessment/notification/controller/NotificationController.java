package com.assessment.notification.controller;

import com.assessment.notification.dto.NotificationRequestDto;
import com.assessment.notification.dto.NotificationResponseDto;
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

    @PostMapping
    public ResponseEntity<NotificationResponseDto> createNotification(
            @Valid @RequestBody NotificationRequestDto requestDto) {
        NotificationResponseDto created = notificationService.createNotification(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
