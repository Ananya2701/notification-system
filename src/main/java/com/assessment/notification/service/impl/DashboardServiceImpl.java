package com.assessment.notification.service.impl;

import com.assessment.notification.dto.DashboardResponseDto;
import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.entity.NotificationType;
import com.assessment.notification.repository.NotificationRepository;
import com.assessment.notification.service.DashboardService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final NotificationRepository notificationRepository;

    public DashboardServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public DashboardResponseDto getDashboard() {
        long total = notificationRepository.count();
        long sent = notificationRepository.countByStatus(NotificationStatus.SENT);
        long failed = notificationRepository.countByStatus(NotificationStatus.FAILED);
        long retrying = notificationRepository.countByStatus(NotificationStatus.RETRYING);
        long pending = notificationRepository.countByStatus(NotificationStatus.PENDING);

        Map<String, DashboardResponseDto.TypeStats> typeWiseStats = new LinkedHashMap<>();

        for (NotificationType type : NotificationType.values()) {
            DashboardResponseDto.TypeStats stats = DashboardResponseDto.TypeStats.builder()
                    .total(notificationRepository.countByType(type))
                    .sent(notificationRepository.countByTypeAndStatus(type, NotificationStatus.SENT))
                    .failed(notificationRepository.countByTypeAndStatus(type, NotificationStatus.FAILED))
                    .pending(notificationRepository.countByTypeAndStatus(type, NotificationStatus.PENDING))
                    .retrying(notificationRepository.countByTypeAndStatus(type, NotificationStatus.RETRYING))
                    .build();
            typeWiseStats.put(type.name(), stats);
        }

        return DashboardResponseDto.builder()
                .totalNotifications(total)
                .sentCount(sent)
                .failedCount(failed)
                .retryingCount(retrying)
                .pendingCount(pending)
                .typeWiseStats(typeWiseStats)
                .build();
    }
}
