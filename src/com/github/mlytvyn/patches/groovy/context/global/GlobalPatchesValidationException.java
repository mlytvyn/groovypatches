package com.github.mlytvyn.patches.groovy.context.global;

public class GlobalPatchesValidationException extends RuntimeException {

    private final GlobalContext globalContext;

    public GlobalPatchesValidationException(final GlobalContext globalContext, final String message) {
        super(message);
        this.globalContext = globalContext;
    }

    public GlobalPatchesValidationException(final GlobalContext globalContext, final String message, final Throwable throwable) {
        super(message, throwable);
        this.globalContext = globalContext;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }
}
