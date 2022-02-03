package com.github.mlytvyn.patches.groovy.util;

import de.hybris.platform.core.initialization.SystemSetupContext;

public interface LogReporter {
    void logInfo(SystemSetupContext context, String message);

    void logError(SystemSetupContext context, String message, Throwable throwable);

    void logInfo(SystemSetupContext context, String message, String color);

    void logWarn(SystemSetupContext context, String message);
}
