

package com.github.mlytvyn.patches.groovy.context.patch;

public class PatchValidationException extends PatchException {

    public PatchValidationException(PatchContextDescriptor patch, String message) {
        super(patch, message);
    }

    public PatchValidationException(PatchContextDescriptor patch, String message, Throwable throwable) {
        super(patch, message, throwable);
    }
}
