package com.yazilimxyz.enterprise_ticket_system.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
