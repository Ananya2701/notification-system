package com.assessment.notification.queue;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class NotificationDispatchWorker implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchWorker.class);

    private final NotificationQueueService queueService;
    private final NotificationDispatcher dispatcher;
    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Override
    public void run(ApplicationArguments args) {
        for (int i = 0; i < 1; i++) {
            executor.submit(() -> {
                while (true) {
                    try {
                        dispatcher.dispatch(queueService.take());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    } catch (Exception e) {
                        log.error("Dispatch failed", e);
                    }
                }
            });
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}