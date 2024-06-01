package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.EmailTemplateImporter;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Set;

public class GlobalContextImportEmailComponentTemplateAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "emailTemplateImporter")
    private EmailTemplateImporter emailTemplateImporter;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        logReporter.logInfo(context, "[Global] started Email Component Templates import");

        globalContext.importEmailComponentTemplates()
            .forEach((key, value) -> importEmailComponentTemplate(context, globalContext, key, value));

        logReporter.logInfo(context, "[Global] completed Email Component Templates import");
    }

    protected void importEmailComponentTemplate(final SystemSetupContext context, final GlobalContext globalContext, final EmailComponentTemplateEnum emailComponentTemplate, final Set<SiteEnum> sites) {
        sites.forEach(site -> {
            final String siteCode = configurationProvider.getSiteCode(site);
            final String template = configurationProvider.getEmailComponentTemplate(emailComponentTemplate);
            logReporter.logInfo(context, String.format("Import Email Component Template for site: %s", siteCode));
            emailTemplateImporter.importEmailTemplate(context, globalContext, template, Collections.singletonMap("siteUid", siteCode));
        });
    }

}
