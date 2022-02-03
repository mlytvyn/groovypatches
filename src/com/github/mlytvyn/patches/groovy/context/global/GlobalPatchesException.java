package com.github.mlytvyn.patches.groovy.context.global;

public class GlobalPatchesException extends RuntimeException {

    private final GlobalContext globalContext;

    public GlobalPatchesException(final GlobalContext globalContext, final String message) {
        super(message);
        this.globalContext = globalContext;
    }

    public GlobalPatchesException(final GlobalContext globalContext, final String message, final Throwable throwable) {
        super(message, throwable);
        this.globalContext = globalContext;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }
}
