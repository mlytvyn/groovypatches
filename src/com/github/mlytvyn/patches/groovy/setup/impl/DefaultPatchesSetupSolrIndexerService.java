package com.github.mlytvyn.patches.groovy.setup.impl;

import com.github.mlytvyn.patches.groovy.setup.PatchesSetupSolrIndexerService;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupSolrIndexerService;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.jalo.config.SolrFacetSearchConfig;
import de.hybris.platform.solrfacetsearch.jalo.indexer.cron.SolrIndexerCronJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultPatchesSetupSolrIndexerService extends DefaultSetupSolrIndexerService implements PatchesSetupSolrIndexerService {

    private static final Logger LOG = LogManager.getLogger(DefaultPatchesSetupSolrIndexerService.class);

    @Override
    public void executeSolrIndexerCronJob(final String solrFacetSearchConfigName, final boolean fullReIndex, final boolean synchronous) {
        final SolrFacetSearchConfig solrFacetConfig = getSolrFacetSearchConfigForName(solrFacetSearchConfigName);
        if (solrFacetConfig != null) {
            executeSolrIndexerCronJob(solrFacetConfig, fullReIndex ? IndexerOperationValues.FULL : IndexerOperationValues.UPDATE, synchronous);
        }
    }

    protected void executeSolrIndexerCronJob(final SolrFacetSearchConfig solrFacetSearchConfig, final IndexerOperationValues indexerOperation, final boolean synchronous) {
        final SolrIndexerCronJob solrIndexerJob = getSolrIndexerJob(solrFacetSearchConfig, indexerOperation);
        if (solrIndexerJob != null
                && (solrIndexerJob.getStatus() == null || !CronJobStatus.RUNNING.getCode().equalsIgnoreCase(solrIndexerJob.getStatus().getName()))) {
            LOG.info("Starting solr {} index operation for [{}] ...", () -> indexerOperation, solrFacetSearchConfig::getName);

            if (!solrIndexerJob.isActiveAsPrimitive()) {
                solrIndexerJob.setActive(true);
            }
            getCronJobService().performCronJob(getModelService().get(solrIndexerJob), synchronous);
            LOG.info("Completed solr {} index operation for [{}]", () -> indexerOperation, solrFacetSearchConfig::getName);
        } else {
            LOG.warn("Solr indexer job is not exists or already running...");
        }
    }
}
