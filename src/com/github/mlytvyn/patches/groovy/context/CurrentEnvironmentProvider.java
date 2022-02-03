package com.github.mlytvyn.patches.groovy.context;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;

public interface CurrentEnvironmentProvider {

    EnvironmentEnum getCurrentEnvironment();
}
