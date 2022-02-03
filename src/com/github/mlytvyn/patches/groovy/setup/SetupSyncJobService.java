package com.github.mlytvyn.patches.groovy.setup;

import de.hybris.platform.servicelayer.cronjob.PerformResult;

public interface SetupSyncJobService extends de.hybris.platform.commerceservices.setup.SetupSyncJobService {

    /**
     * Custom extension of the [y] setup sync job implementation with possibility to force FULL catalog sync
     *
     * @param catalogId  catalog UID
     * @param forcedSync true/false
     * @return sync result
     */
    PerformResult executeCatalogSyncJob(String catalogId, boolean forcedSync);
}
