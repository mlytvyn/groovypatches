package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.CurrentEnvironmentProvider;

/**
 * Always override this default implementation in the project.
 */
public class DefaultCurrentEnvironmentProvider implements CurrentEnvironmentProvider {

    @Override
    public EnvironmentEnum getCurrentEnvironment() {
        return EnvironmentEnum.LOCAL;
    }
}
