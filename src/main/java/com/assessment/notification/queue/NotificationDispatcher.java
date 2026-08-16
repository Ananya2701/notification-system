package com.assessment.notification.queue;

import com.assessment.notification.entity.Notification;
import com.assessment.notification.entity.NotificationStatus;
import com.assessment.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);
    private static final int FAILURE_PERCENTAGE = 30;

    private final NotificationRepository notificationRepository;
    private final Random random = new Random();


    public void dispatch(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null) {
            log.warn("Dispatch skipped - notification not found for notification id = {}", notificationId);
            return;
        }

        boolean simulatedFailure = random.nextInt(100) < FAILURE_PERCENTAGE;
        notification.setStatus(simulatedFailure ? NotificationStatus.FAILED : NotificationStatus.SENT);
        notificationRepository.save(notification);

        if (simulatedFailure) {
            log.warn("Notification id={} delivery FAILED", notificationId);
        } else {
            log.info("Notification id={} delivery SENT", notificationId);
        }
    }
}
