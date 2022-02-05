package com.github.mlytvyn.patches.groovy.setup;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalPatchesException;
import com.github.mlytvyn.patches.groovy.context.global.GlobalPatchesValidationException;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.PatchValidationException;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseImporter;
import com.github.mlytvyn.patches.groovy.context.release.ReleasesCollector;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.github.mlytvyn.patches.groovy.context.ContextSerializationException;
import com.github.mlytvyn.patches.groovy.context.GroovyPatchContextService;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.List;

public abstract class GroovyPatchesSystemSetup {
    public static final HashFunction MD5 = Hashing.md5();
    private static final Logger LOG = LogManager.getLogger();

    @Resource(name = "releasesCollector")
    protected ReleasesCollector<GlobalContext> releasesCollector;
    @Resource(name = "releaseImporter")
    protected ReleaseImporter releaseImporter;
    @Resource(name = "logReporter")
    protected LogReporter logReporter;
    @Resource(name = "groovyPatchContextService")
    protected GroovyPatchContextService groovyPatchContextService;
    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;
    @Resource(name = "groovyPatchesGlobalContextBeforeActions")
    protected List<GlobalContextAction<GlobalContext>> beforeActions;
    @Resource(name = "groovyPatchesGlobalContextAfterActions")
    protected List<GlobalContextAction<GlobalContext>> afterActions;

    protected void executePatches(final SystemSetupContext context, final String pattern) {
        try {
            applyPatches(context, configurationService.getConfiguration().getString("patches.groovy.project.extension.name") + pattern);
        } catch (final ContextSerializationException e) {
            LOG.fatal(e.getMessage(), e);
        }
    }

    private void applyPatches(final SystemSetupContext context, final String locationPattern) throws ContextSerializationException {
        try {
            final GlobalContext globalContext = groovyPatchContextService.restoreOrCreateGlobalContext();

            releasesCollector.collect(globalContext, locationPattern);

            if (globalContext.releases().isEmpty()) {
                logReporter.logInfo(context, "No pending patches found.");
                return;
            }

            executeActions(context, globalContext, beforeActions, "before");
            releaseImporter.execute(context, globalContext.releases());
            executeActions(context, globalContext, afterActions, "after");
        } catch (final GlobalPatchesValidationException e) {
            logReporter.logError(context, e.getMessage(), e);
        } catch (final PatchValidationException e) {
            logReporter.logError(context, e.getMessage(), e);
            groovyPatchContextService.serializeGlobalContext(e.getGlobalContext());
        } catch (final GlobalPatchesException e) {
            logReporter.logError(context, e.getMessage(), e);
            groovyPatchContextService.serializeGlobalContext(e.getGlobalContext());
        } catch (final PatchException e) {
            logReporter.logError(context, e.getMessage(), e);
            final ReleaseContext release = e.getReleaseContext();
            groovyPatchContextService.serializeGlobalContext(e.getGlobalContext());
            groovyPatchContextService.serializeReleaseContext(release);
        } catch (final Exception e) {
            logReporter.logError(context, "Error during patch execution: " + e.getMessage(), e);
        }
    }

    private void executeActions(final SystemSetupContext context, final GlobalContext globalContext, final List<GlobalContextAction<GlobalContext>> actions, final String name) {
        logReporter.logInfo(context, String.format("Started global %s actions", name), "darkviolet");

        actions.forEach(action -> action.execute(context, globalContext));

        logReporter.logInfo(context, String.format("Completed global %s actions", name), "darkviolet");
    }

}
