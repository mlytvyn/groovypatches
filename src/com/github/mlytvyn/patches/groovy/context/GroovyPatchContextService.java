package com.github.mlytvyn.patches.groovy.context;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;

public interface GroovyPatchContextService {
    <T extends GlobalContext> T restoreOrCreateGlobalContext() throws ContextSerializationException;

    <T extends GlobalContext> void serializeGlobalContext(T globalContext) throws ContextSerializationException;

    <T extends ReleaseContext> T restoreOrCreateReleaseContext(String releaseVersion, String releaseId) throws ContextSerializationException;

    <T extends ReleaseContext> void serializeReleaseContext(T releaseContext) throws ContextSerializationException;
}
