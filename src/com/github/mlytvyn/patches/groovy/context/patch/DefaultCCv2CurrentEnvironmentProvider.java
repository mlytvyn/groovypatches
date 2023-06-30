package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.CurrentEnvironmentProvider;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Locale;

public class DefaultCCv2CurrentEnvironmentProvider implements CurrentEnvironmentProvider {

    private static final Logger LOG = LogManager.getLogger(DefaultCCv2CurrentEnvironmentProvider.class);

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public EnvironmentEnum getCurrentEnvironment() {
        try {
            final String environmentCode = configurationService.getConfiguration().getString("modelt.environment.code", EnvironmentEnum.LOCAL.name());
            return EnvironmentEnum.valueOf(environmentCode.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            LOG.warn(e);
            return EnvironmentEnum.LOCAL;
        }
    }
}
