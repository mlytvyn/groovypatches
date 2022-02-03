package com.github.mlytvyn.patches.groovy.context.global;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;

public interface GlobalContextFactory<T extends GlobalContext> {

    T createContext(EnvironmentEnum currentEnvironment);
}
