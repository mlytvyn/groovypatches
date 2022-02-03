package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class GlobalContextValidateAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "releaseContextValidateAction")
    private ReleaseContextAction releaseValidateAction;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        logReporter.logInfo(context, "Started patches validation");

        globalContext.getReleases().forEach(release -> releaseValidateAction.execute(context, release));

        logReporter.logInfo(context, "Completed patches validation");
    }

}
