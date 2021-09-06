

package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class DefaultLogReporter implements LogReporter {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public void logInfo(final SystemSetupContext context, final String message) {
        logInfo(context, message, "black");
    }

    @Override
    public void logError(final SystemSetupContext context, final String message, final Throwable throwable) {
        LOG.error(message, throwable);
        Optional.of(context.getJspContext())
            .ifPresent(jspContext -> jspContext.println("<font color='red'>" + message + "</font>"));
    }

    @Override
    public void logInfo(final SystemSetupContext context, final String message, final String color) {
        LOG.info(message);
        Optional.of(context.getJspContext())
            .ifPresent(jspContext -> jspContext.println("<font color='" + color + "'>" + message + "</font>"));
    }

    @Override
    public void logWarn(final SystemSetupContext context, final String message) {
        LOG.warn(message);
        Optional.of(context.getJspContext())
            .ifPresent(jspContext -> jspContext.println("<font color='darkorange'>" + message + "</font>"));
    }
}
