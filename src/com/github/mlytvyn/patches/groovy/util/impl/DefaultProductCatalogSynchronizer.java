package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import com.github.mlytvyn.patches.groovy.setup.SetupSyncJobService;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.ContentCatalogSynchronizer;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import com.github.mlytvyn.patches.groovy.util.ParallelPoolExecutor;
import com.github.mlytvyn.patches.groovy.util.ProductCatalogSynchronizer;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Map;

public class DefaultProductCatalogSynchronizer implements ProductCatalogSynchronizer {

    private static final Logger LOG = LogManager.getLogger(DefaultProductCatalogSynchronizer.class);

    @Resource(name = "extendedSetupSyncJobService")
    protected SetupSyncJobService extendedSetupSyncJobService;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "groovyPatchesParallelPoolExecutor")
    private ParallelPoolExecutor parallelPoolExecutor;
    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void synchronize(final SystemSetupContext context, final Map<ProductCatalogEnum, Boolean> productCatalogsToBeSynced, final boolean parallelSync) {
        if (productCatalogsToBeSynced.isEmpty()) {
            return;
        }
        if (parallelSync) {
            parallelPoolExecutor.execute(context).accept(() -> productCatalogsToBeSynced.entrySet().parallelStream()
                .forEach(entry -> synchronizeProductCatalog(context, entry)));
        } else {
            productCatalogsToBeSynced.entrySet().forEach(entry -> synchronizeProductCatalog(context, entry));
        }
    }

    private void synchronizeProductCatalog(final SystemSetupContext context, final Map.Entry<ProductCatalogEnum, Boolean> entry) {
        final String catalogUid = configurationProvider.getProductCatalogId(entry.getKey());
        logReporter.logInfo(context, String.format("Starting catalog %s sync", catalogUid));
        synchronizeProductCatalog(context, catalogUid, entry.getValue());
        logReporter.logInfo(context, String.format("Completed catalog %s sync", catalogUid));
    }

    private void synchronizeProductCatalog(final SystemSetupContext context, final String catalogId, final boolean forcedSync) {
        logReporter.logInfo(context, String.format(forcedSync ? "Forced synchronization of the product catalog: %s" : "Synchronization of the product catalog: %s", catalogId));
        try {
            extendedSetupSyncJobService.createProductCatalogSyncJob(catalogId);
            extendedSetupSyncJobService.executeCatalogSyncJob(catalogId, forcedSync);
        } catch (final UnknownIdentifierException e) {
            LOG.info("Could not synchronize {}", catalogId);
        }
    }
}
