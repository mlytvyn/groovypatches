package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.EmailTemplateImporter;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

public class GlobalContextImportEmailTemplateAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "emailTemplateImporter")
    private EmailTemplateImporter emailTemplateImporter;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        logReporter.logInfo(context, "Starting Email Templates import");

        globalContext.importEmailTemplates().stream()
                .map(emailTemplate -> {
                    final String emailTemplateFile = configurationProvider.getEmailTemplate(emailTemplate);
                    if (StringUtils.isNotBlank(emailTemplateFile)) return emailTemplateFile;

                    logReporter.logWarn(context, "Unable to find email template file: " + emailTemplate + ". Ensure that property 'patches.groovy.emailTemplate." + emailTemplate + ".template' is set and valid.");

                    return null;
                })
                .filter(Objects::nonNull)
                .forEach(template -> emailTemplateImporter.importEmailTemplate(context, globalContext, template, Collections.emptyMap()));

        logReporter.logInfo(context, "Completed Email Templates import");
    }

}
