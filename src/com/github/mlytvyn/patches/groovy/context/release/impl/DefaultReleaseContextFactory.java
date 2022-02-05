package com.github.mlytvyn.patches.groovy.context.release.impl;

import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContextFactory;

public class DefaultReleaseContextFactory implements ReleaseContextFactory<ReleaseContext> {

    @Override
    public ReleaseContext createContext(final String releaseVersion, final String releaseId) {
        return ReleaseContext.of(releaseVersion, releaseId);
    }
}
