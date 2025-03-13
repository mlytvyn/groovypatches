package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.cronjob.util.TypeRemovalUtil;

import javax.annotation.Resource;
import java.util.Map;

public class GlobalContextRemoveOrphanedTypesAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        if (globalContext.removeOrphanedTypes()) {
            logReporter.logInfo(context, "Started orphaned types removal");

            final Map<String, String> removedTypes = TypeRemovalUtil.removeOrphanedTypes(true, true);
            removedTypes.forEach((key, value) -> logReporter.logInfo(context, String.format("Type [%s] removal status: %s", key, value == null)));

            logReporter.logInfo(context, "Completed orphaned types removal");
        }
    }

}
