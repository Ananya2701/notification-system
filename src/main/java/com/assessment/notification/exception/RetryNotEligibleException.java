package com.assessment.notification.exception;

public class RetryNotEligibleException extends RuntimeException {
    public RetryNotEligibleException(String message) {
        super(message);
    }
}
