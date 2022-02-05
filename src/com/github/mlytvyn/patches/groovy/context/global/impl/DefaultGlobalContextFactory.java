package com.github.mlytvyn.patches.groovy.context.global.impl;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContextFactory;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;

import javax.annotation.Resource;

public class DefaultGlobalContextFactory implements GlobalContextFactory<GlobalContext> {

    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;

    @Override
    public GlobalContext createContext(final EnvironmentEnum currentEnvironment) {
        return prepareContext(currentEnvironment).build();
    }

    protected GlobalContext.GlobalContextBuilder prepareContext(final EnvironmentEnum currentEnvironment) {
        final Configuration configuration = configurationService.getConfiguration();
        return GlobalContext.builder(currentEnvironment)
                .impexImportConfig(ImpexImportConfig.builder()
                        .failOnError(configuration.getBoolean("patches.groovy.impex.import.configuration.failOnError", true))
                        .enableCodeExecution(configuration.getBoolean("patches.groovy.impex.import.configuration.enableCodeExecution", true))
                        .legacyMode(configuration.getBoolean("patches.groovy.impex.import.configuration.legacyMode", false))
                        .removeOnSuccess(configuration.getBoolean("patches.groovy.impex.import.configuration.removeOnSuccess", false))
                        .synchronous(configuration.getBoolean("patches.groovy.impex.import.configuration.synchronous", true))
                        .errorIfMissing(configuration.getBoolean("patches.groovy.impex.import.configuration.errorIfMissing", true))
                        .build());
    }
}
