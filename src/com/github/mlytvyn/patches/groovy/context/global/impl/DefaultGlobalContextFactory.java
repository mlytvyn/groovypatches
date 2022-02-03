package com.github.mlytvyn.patches.groovy.context.global.impl;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContextFactory;

public class DefaultGlobalContextFactory implements GlobalContextFactory<GlobalContext> {

    @Override
    public GlobalContext createContext(final EnvironmentEnum currentEnvironment) {
        return new GlobalContext(currentEnvironment);
    }
}
