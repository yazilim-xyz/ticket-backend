package com.yazilimxyz.enterprise_ticket_system.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
