package com.github.mlytvyn.patches.groovy.context;

public class ContextSerializationException extends RuntimeException {

    public ContextSerializationException(final String message) {
        super(message);
    }

    public ContextSerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
