package com.github.mlytvyn.patches.groovy.context.impex;

public class ImpexImportException extends RuntimeException {

    public ImpexImportException(final String message) {
        super(message);
    }

    public ImpexImportException(final String message, final Exception e) {
        super(message, e);
    }
}
