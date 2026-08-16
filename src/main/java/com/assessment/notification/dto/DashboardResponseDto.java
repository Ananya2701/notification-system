package com.assessment.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {
    private long totalNotifications;
    private long sentCount;
    private long failedCount;
    private long retryingCount;
    private long pendingCount;
    private Map<String, TypeStats> typeWiseStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeStats {
        private long total;
        private long sent;
        private long failed;
        private long pending;
        private long retrying;
    }
}
