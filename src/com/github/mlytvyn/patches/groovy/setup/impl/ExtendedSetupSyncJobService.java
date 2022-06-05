package com.github.mlytvyn.patches.groovy.setup.impl;

import com.github.mlytvyn.patches.groovy.setup.SetupSyncJobService;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.catalog.jalo.SyncItemJob;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtendedSetupSyncJobService extends DefaultSetupSyncJobService implements SetupSyncJobService {

    private static final Logger LOG = LogManager.getLogger(ExtendedSetupSyncJobService.class);

    @Override
    public PerformResult executeCatalogSyncJob(final String catalogId, final boolean forcedSync) {
        return forcedSync
                ? executeCatalogForcedSyncJob(catalogId)
                : executeCatalogSyncJob(catalogId);
    }

    /**
     * Copy-paste from [y] implementation, except ForceUpdate flag
     */
    private PerformResult executeCatalogForcedSyncJob(final String catalogId) {
        final SyncItemJob catalogSyncJob = getCatalogSyncJob(catalogId);
        if (catalogSyncJob == null) {
            LOG.error("Couldn't find 'SyncItemJob' for catalog [{}]", catalogId);
            return new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.UNKNOWN);
        } else {
            final SyncItemCronJob syncJob = getLastFailedSyncCronJob(catalogSyncJob);
            syncJob.setLogToDatabase(false);
            syncJob.setLogToFile(false);
            syncJob.setForceUpdate(true);

            LOG.info("Created cronjob [{}] to synchronize catalog [{}] staged to online version.", syncJob::getCode, () -> catalogId);

            syncJob.setConfigurator(new FullSyncConfigurator(catalogSyncJob));

            LOG.info("Starting synchronization, this may take a while ...");

            catalogSyncJob.perform(syncJob, true);

            LOG.info("Synchronization complete for catalog [{}]", catalogId);

            final CronJobResult result = getModelService().get(syncJob.getResult());
            final CronJobStatus status = getModelService().get(syncJob.getStatus());
            return new PerformResult(result, status);
        }
    }
}
