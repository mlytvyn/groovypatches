package com.github.mlytvyn.patches.groovy.cronjob;

import com.github.mlytvyn.patches.groovy.model.PatchesFullReIndexCronJobModel;
import com.github.mlytvyn.patches.groovy.setup.PatchesSetupSolrIndexerService;
import com.github.mlytvyn.patches.groovy.solrfacetsearch.daos.PatchesSolrFacetSearchConfigDao;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static org.apache.logging.log4j.LogManager.getLogger;

public class PatchesFullReIndexJobPerformable extends AbstractJobPerformable<PatchesFullReIndexCronJobModel> {

    private static final Logger LOG = getLogger();

    @Resource(name = "patchesSolrFacetSearchConfigDao")
    private PatchesSolrFacetSearchConfigDao patchesSolrFacetSearchConfigDao;
    @Resource(name = "modelService")
    private ModelService modelService;
    @Resource(name = "patchesSetupSolrIndexerService")
    private PatchesSetupSolrIndexerService patchesSetupSolrIndexerService;

    @Override
    public PerformResult perform(final PatchesFullReIndexCronJobModel cronJob) {
        LOG.info("Checking for pending full reindex'es for recently applied patches.");
        final List<SolrFacetSearchConfigModel> configs = getAllSolrFacetSearchConfigsToReindex();

        if (CollectionUtils.isNotEmpty(configs)) {
            LOG.info("Starting solr full reindex cronjobs for recently applied patches.");
            configs.forEach(config -> {
                LOG.info("Starting solr full reindex for {}", config::getName);
                config.setReIndexAfterStartUp(false);
                modelService.save(config);
                patchesSetupSolrIndexerService.executeSolrIndexerCronJob(config.getName(), true, true);
                LOG.info("Finished solr full reindex for {}", config::getName);
            });
            LOG.info("Finished solr full reindex cronjobs for recently applied patches.");
        } else {
            LOG.info("There are no pending execution full reindex requests for recently applied patches.");
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

    private List<SolrFacetSearchConfigModel> getAllSolrFacetSearchConfigsToReindex() {
        try {
            return patchesSolrFacetSearchConfigDao.findAllSolrFacetSearchConfigsToReindex();
        } catch (final FlexibleSearchException e) {
            LOG.warn("System is not yet updated, cannot execute FXS with required parameters. Root cause: {}", e::getMessage);
        }
        return Collections.emptyList();
    }

}
