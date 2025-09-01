package com.algaposts.text_processor.infrastructure.messaging.exception;

public class ResultPublishingException extends RuntimeException {
    
    public ResultPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}