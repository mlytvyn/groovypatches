package com.github.mlytvyn.patches.groovy.event.impl;

import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum;
import com.github.mlytvyn.patches.groovy.solrfacetsearch.config.PatchesFacetSearchConfigService;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.config.exceptions.SolrFacetSearchMisconfigurationException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrExtIndexerCronJobModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class PatchesPendingPartialReIndexEventListener extends AbstractPatchesPendingReIndexEventListener<AfterInitializationEndEvent> {

    private static final Logger LOG = LogManager.getLogger(PatchesPendingPartialReIndexEventListener.class);

    @Resource(name = "cronJobService")
    private CronJobService cronJobService;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "patchesFacetSearchConfigService")
    private PatchesFacetSearchConfigService patchesFacetSearchConfigService;

    @Override
    protected void onEvent(final AfterInitializationEndEvent afterInitializationEndEvent) {
        final List<SolrExtIndexerCronJobModel> cronJobs = EnumSet.allOf(SolrIndexedTypeEnum.class).stream()
                .map(this::retrieveIndexingCronJob)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        if (cronJobs.isEmpty()) return;

        cronJobs.forEach(this::replaceInstantTrigger);

        modelService.saveAll(cronJobs);
    }

    private Optional<SolrExtIndexerCronJobModel> retrieveIndexingCronJob(final SolrIndexedTypeEnum solrIndexedType) {
        try {
            final String cronJobName = configurationProvider.getSolrIndexedTypePartialCronJobPrefix(solrIndexedType);
            final SolrExtIndexerCronJobModel cronJob = (SolrExtIndexerCronJobModel) cronJobService.getCronJob(cronJobName);
            if (cronJob.getFacetSearchConfig().isReIndexAfterStartUp()) {
                LOG.info("Full reindex is scheduled. Skipping partial update. [indexed type: {}]", solrIndexedType);
                final Collection<TriggerModel> triggers = CollectionUtils.emptyIfNull(cronJob.getTriggers());

                cronJob.setActive(false);

                modelService.removeAll(triggers);
                modelService.save(cronJob);
                return Optional.empty();
            }

            removeUnresolvedIndexedProperties(cronJob);

            return Optional.of(cronJob)
                    .filter(cron -> !cron.getIndexedProperties().isEmpty())
                    .filter(cron -> cron.getStatus() == CronJobStatus.UNKNOWN)
                    .filter(cron -> BooleanUtils.isTrue(cron.getActive()));
        } catch (final UnknownIdentifierException e) {
            return Optional.empty();
        }
    }

    private void removeUnresolvedIndexedProperties(final SolrExtIndexerCronJobModel cronJob) {
        final SolrFacetSearchConfigModel facetSearchConfig = cronJob.getFacetSearchConfig();
        final String configName = facetSearchConfig.getName();
        try {
            final FacetSearchConfig configuration = patchesFacetSearchConfigService.getConfiguration(configName);
            final IndexedType indexedType = patchesFacetSearchConfigService.resolveIndexedType(configuration, cronJob.getIndexedType());
            final Collection<String> unresolvableProperties = patchesFacetSearchConfigService.findUnresoledIndexedProperties(indexedType, cronJob.getIndexedProperties());
            if (!unresolvableProperties.isEmpty()) {
                LOG.warn("Indexed properties could not be resolved. Skipping. [unresolvable properties: {} | config: {}]", () -> String.join(", ", unresolvableProperties), () -> configName);
                final Collection<String> resolvableProperties = cronJob.getIndexedProperties().stream()
                        .filter(not(unresolvableProperties::contains))
                        .collect(Collectors.toSet());
                cronJob.setIndexedProperties(resolvableProperties);
                modelService.save(cronJob);
            }
        } catch (final FacetConfigServiceException e) {
            throw new SolrFacetSearchMisconfigurationException(String.format("Could not get [%s] facet search configuration", configName), e);
        }
    }
}
