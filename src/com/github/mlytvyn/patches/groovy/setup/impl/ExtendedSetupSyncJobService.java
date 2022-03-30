package com.github.mlytvyn.patches.groovy.setup.impl;

import com.github.mlytvyn.patches.groovy.setup.SetupSyncJobService;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupSyncJobService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: Hybris 1905+ implementation, adjust accordingly
public class ExtendedSetupSyncJobService extends DefaultSetupSyncJobService implements SetupSyncJobService {

    private static final Logger LOG = LogManager.getLogger(ExtendedSetupSyncJobService.class);

    @Override
    public PerformResult executeCatalogSyncJob(final String catalogId, final boolean forcedSync) {
        return forcedSync ? executeCatalogForcedSyncJob(catalogId) : executeCatalogSyncJob(catalogId);
    }

    /**
     * Copy-paste from [y] implementation, except ForceUpdate flag
     */
    private PerformResult executeCatalogForcedSyncJob(final String catalogId) {
        return executeCatalogSyncJob(catalogId);
//        final SyncItemJobModel catalogSyncJob = getCatalogSyncJob(catalogId);
//        if (catalogSyncJob == null) {
//            LOG.error("Couldn't find 'SyncItemJob' for catalog [{}]", catalogId);
//            return new PerformResult(CronJobResult.UNKNOWN, CronJobStatus.UNKNOWN);
//        } else {
//            final SyncItemCronJobModel syncJob = getLastFailedSyncCronJob(catalogSyncJob);
//            syncJob.setLogToDatabase(false);
//            syncJob.setLogToFile(false);
//            syncJob.setForceUpdate(true);
//
//            LOG.info("Created cronjob [{}] to synchronize catalog [{}] staged to online version.", syncJob::getCode, () -> catalogId);
//
//            syncJob.setForceUpdate(true);
//
//            LOG.info("Starting synchronization, this may take a while ...");
//
//            getCronJobService().performCronJob(syncJob, true);
//
//            LOG.info("Synchronization complete for catalog [{}]", catalogId);
//
//            final CronJobResult result = syncJob.getResult();
//            final CronJobStatus status = syncJob.getStatus();
//
//            return new PerformResult(result, status);
//        }
    }
}
