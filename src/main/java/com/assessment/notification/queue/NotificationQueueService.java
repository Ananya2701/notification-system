package com.assessment.notification.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class NotificationQueueService {

    private static final Logger log = LoggerFactory.getLogger(NotificationQueueService.class);

    private final BlockingQueue<Long> queue = new LinkedBlockingQueue<>();

    public void enqueue(Long notificationId) {
        queue.offer(notificationId);
        log.info("Notification id={} queued for dispatch", notificationId);
    }

    public Long take() throws InterruptedException {
        return queue.take();
    }
}
