package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.setup.SetupSyncJobService;
import com.github.mlytvyn.patches.groovy.util.ContentCatalogSynchronizer;
import com.github.mlytvyn.patches.groovy.util.ParallelPoolExecutor;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Map;

public class DefaultContentCatalogSynchronizer implements ContentCatalogSynchronizer {

    private static final Logger LOG = LogManager.getLogger();

    @Resource(name = "extendedSetupSyncJobService")
    protected SetupSyncJobService extendedSetupSyncJobService;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "groovyPatchesParallelPoolExecutor")
    private ParallelPoolExecutor parallelPoolExecutor;
    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void synchronize(final SystemSetupContext context, final Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSynced, final boolean parallelSync) {
        if (contentCatalogsToBeSynced.isEmpty()) {
            return;
        }
        if (parallelSync) {
            parallelPoolExecutor.execute(context).accept(() -> contentCatalogsToBeSynced.entrySet().parallelStream()
                .forEach(entry -> synchronizeContentCatalog(context, entry)));
        } else {
            contentCatalogsToBeSynced.entrySet().forEach(entry -> synchronizeContentCatalog(context, entry));
        }
    }

    private void synchronizeContentCatalog(final SystemSetupContext context, final Map.Entry<ContentCatalogEnum, Boolean> entry) {
        final String catalogUid = configurationProvider.getContentCatalogId(entry.getKey());
        logReporter.logInfo(context, String.format("Starting catalog %s sync", catalogUid));
        synchronizeContentCatalog(context, catalogUid, entry.getValue());
        logReporter.logInfo(context, String.format("Completed catalog %s sync", catalogUid));
    }

    private void synchronizeContentCatalog(final SystemSetupContext context, final String catalogId, final boolean forcedSync) {
        logReporter.logInfo(context, String.format(forcedSync ? "Forced synchronization of the content catalog: %s" : "Synchronization of the content catalog: %s", catalogId));
        try {
            extendedSetupSyncJobService.createContentCatalogSyncJob(catalogId);
            extendedSetupSyncJobService.executeCatalogSyncJob(catalogId, forcedSync);
        } catch (final UnknownIdentifierException e) {
            LOG.info("Could not synchronize {}", catalogId);
        }
    }
}
