package com.github.mlytvyn.patches.groovy.context.release.impl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseImporter;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

public class DefaultReleaseImporter implements ReleaseImporter {

    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;
    @Resource(name = "releaseContextApplyPatchesAction")
    protected ReleaseContextAction releaseApplyPatchesAction;
    @Resource(name = "groovyPatchesReleaseContextBeforeActions")
    protected List<ReleaseContextAction> beforeActions;
    @Resource(name = "groovyPatchesReleaseContextAfterActions")
    protected List<ReleaseContextAction> afterActions;
    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void execute(final SystemSetupContext context, final List<ReleaseContext> releases) {
        for (final ReleaseContext release : releases) {
            try {
                final Set<PatchContextDescriptor> patches = release.getPatches();

                if (configurationService.getConfiguration().getBoolean("log4j2.threadContext.PatchesId.enabled", false)) {
                    ThreadContext.put("PatchesId", release.getId());
                }
                logReporter.logInfo(context, String.format("[Release: %s] started [%s] patches", release.getId(), patches.size()), "green");

                executeActions(context, release, beforeActions, "before");
                releaseApplyPatchesAction.execute(context, release);
                executeActions(context, release, afterActions, "after");

                logReporter.logInfo(context, String.format("[Release: %s] completed [%s] patches", release.getId(), patches.size()), "green");
            } finally {
                ThreadContext.remove("PatchesId");
            }
        }
    }

    private void executeActions(final SystemSetupContext context, final ReleaseContext release, final List<ReleaseContextAction> actions, final String name) {
        logReporter.logInfo(context, String.format("[Release: %s] started %s actions", release.getId(), name), "darkviolet");

        actions.forEach(action -> action.execute(context, release));

        logReporter.logInfo(context, String.format("[Release: %s] completed %s actions", release.getId(), name), "darkviolet");
    }


}
