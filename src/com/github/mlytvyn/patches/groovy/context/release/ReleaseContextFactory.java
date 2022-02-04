package com.github.mlytvyn.patches.groovy.context.release;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;

public interface ReleaseContextFactory<T extends ReleaseContext> {

    T createContext(String releaseVersion, String releaseId);
}
