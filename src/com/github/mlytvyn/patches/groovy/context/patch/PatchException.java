package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;

public class PatchException extends RuntimeException {

    private final PatchContextDescriptor patch;

    public PatchException(final PatchContextDescriptor patch, final String message) {
        super(adjustMessage(patch, message));
        this.patch = patch;
    }

    public PatchException(final PatchContextDescriptor patch, final String message, final Throwable throwable) {
        super(adjustMessage(patch, message), throwable);
        this.patch = patch;
    }

    private static String adjustMessage(final PatchContextDescriptor patch, final String message) {
        return "[" + patch.getName() + "] " + message;
    }

    public PatchContextDescriptor getPatch() {
        return patch;
    }

    public ReleaseContext getReleaseContext() {
        return patch.getReleaseContext();
    }

    public GlobalContext getGlobalContext() {
        return patch.getGlobalContext();
    }
}
