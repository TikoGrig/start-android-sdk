package com.payfort.start.error;

/**
 * This exception is thrown during Start API call.
 */
public class StartApiException extends Exception {

    public StartApiException(String message) {
        super(message);
    }

    public StartApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartApiException(Throwable cause) {
        super(cause);
    }
}
