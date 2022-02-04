package com.github.mlytvyn.patches.groovy.context.release.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.Resource;
import java.util.List;

public class ReleaseContextApplyPatchesAction implements ReleaseContextAction {

    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;
    @Resource(name = "groovyPatchesPatchContextActions")
    protected List<PatchAction<PatchContextDescriptor>> actions;
    @Resource(name = "patchSaveSystemSetupAuditAction")
    protected PatchAction<PatchContextDescriptor> patchSaveSystemSetupAuditAction;
    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void execute(final SystemSetupContext context, final ReleaseContext release) {
        for (final PatchContextDescriptor patch : release.patches()) {
            try {
                if (configurationService.getConfiguration().getBoolean("log4j2.threadContext.PatchId.enabled", false)) {
                    ThreadContext.put("PatchId", patch.getId());
                }

                logReporter.logInfo(context, String.format("[Patch : %s] started", patch.getName()), "teal");

                executeSinglePatch(context, patch);

                // TODO: if it failed do not add it or mark somehow...
                patchSaveSystemSetupAuditAction.execute(context, patch);
            } catch (final Exception e) {
                throw new PatchException(patch, e.getMessage(), e);
            } finally {
                logReporter.logInfo(context, String.format("[Patch : %s] completed", patch.getName()), "teal");
                ThreadContext.remove("PatchId");
            }
        }
    }

    private void executeSinglePatch(final SystemSetupContext context, final PatchContextDescriptor patch) {
        if (patch.isNotApplicable()) {
            logReporter.logInfo(
                context,
                String.format(
                    "[Patch : %s] is not applicable for current environment [current: %s | allowed: %s]",
                    patch.getName(), patch.getCurrentEnvironment(), patch.getEnvironments()
                ),
                "teal");
            return;
        }
        actions.forEach(action -> action.execute(context, patch));

        patch.getNestedPatch()
            .ifPresent(nestedPatch -> executeSinglePatch(context, nestedPatch));

        patch.getEnvironmentPatch()
            .ifPresent(environmentPatch -> executeSinglePatch(context, environmentPatch));
    }

}
