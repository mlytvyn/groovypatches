package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexerQueryModel;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrExtIndexerCronJobModel;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalContextSolrPartialReIndexAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "modelService")
    private ModelService modelService;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;
    @Resource(name = "cronJobService")
    private CronJobService cronJobService;
    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;
    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        final Map<SolrIndexedTypeEnum, Set<String>> solrIndexedTypesForReIndex = globalContext.partiallyReIndexedSolrIndexedTypes();
        if (solrIndexedTypesForReIndex.isEmpty()) return;

        logReporter.logInfo(context, "[Global] Started SOLR request for partial reindex");

        final List<SolrExtIndexerCronJobModel> cronJobs = solrIndexedTypesForReIndex.entrySet().stream()
                .map(entry -> preparePartialIndexingCronJob(context, entry.getKey(), entry.getValue()))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        if (!cronJobs.isEmpty()) {
            final List<TriggerModel> oldTriggers = cronJobs.stream()
                    .map(CronJobModel::getTriggers)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            modelService.removeAll(oldTriggers);
            modelService.saveAll(cronJobs);
        }

        logReporter.logInfo(context, "[Global] Completed SOLR request for partial reindex");
    }

    protected Optional<SolrExtIndexerCronJobModel> preparePartialIndexingCronJob(final SystemSetupContext context, final SolrIndexedTypeEnum indexedType, final Set<String> indexedProperties) {
        final String cronJobName = configurationProvider.getSolrIndexedTypePartialCronJobPrefix(indexedType);

        try {
            final SolrExtIndexerCronJobModel cronJob = (SolrExtIndexerCronJobModel) cronJobService.getCronJob(cronJobName);
            final SolrIndexedTypeModel solrIndexedType = getSolrIndexedType(indexedType);
            final IndexerOperationValues indexerOperation = getIndexerOperationValues(context);
            final SolrIndexerQueryModel solrIndexerQuery = getSolrIndexerQuery(solrIndexedType, indexerOperation);

            // to ensure that partial reindex job is always using latest index query we have to retrieve corresponding SolrIndexedType
            cronJob.setQuery(solrIndexerQuery.getQuery());
            cronJob.setStatus(CronJobStatus.UNKNOWN);
            cronJob.setIndexedProperties(indexedProperties);
            cronJob.setActive(!indexedProperties.isEmpty());

            return Optional.of(cronJob);
        } catch (final SystemException e) {
            logReporter.logInfo(context, "Cannot find SolrExtIndexerCronJob for name: " + cronJobName + ". Automatically creating corresponding cron job.");

            return tryCreatePartialIndexingCronJob(context, indexedType, indexedProperties, cronJobName);
        }
    }

    protected Optional<SolrExtIndexerCronJobModel> tryCreatePartialIndexingCronJob(final SystemSetupContext context, final SolrIndexedTypeEnum indexedTypeKey, final Set<String> indexedProperties, final String cronJobName) {
        try {
            final SolrIndexedTypeModel indexedType = getSolrIndexedType(indexedTypeKey);
            final SolrFacetSearchConfigModel facetSearchConfig = indexedType.getSolrFacetSearchConfig();

            if (facetSearchConfig == null) {
                logReporter.logWarn(context, "SolrFacetSearchConfig is not set for SolrIndexedType '" + indexedType.getIdentifier() + "'.");
                return Optional.empty();
            }

            final IndexerOperationValues indexerOperation = getIndexerOperationValues(context);
            final SolrIndexerQueryModel solrIndexerQuery = getSolrIndexerQuery(indexedType, indexerOperation);
            final ServicelayerJobModel serviceLayerJob = retrieveServiceLayerJob();
            final LanguageModel sessionLanguage = getSessionLanguage();

            final SolrExtIndexerCronJobModel cronJob = modelService.create(SolrExtIndexerCronJobModel.class);
            cronJob.setCode(cronJobName);
            cronJob.setIndexedType(computeIndexedTypeName(indexedType));
            cronJob.setIndexerOperation(IndexerOperationValues.PARTIAL_UPDATE);
            cronJob.setSessionLanguage(sessionLanguage);
            cronJob.setJob(serviceLayerJob);
            cronJob.setFacetSearchConfig(facetSearchConfig);
            cronJob.setSingleExecutable(true);
            cronJob.setQuery(solrIndexerQuery.getQuery());
            cronJob.setIndexedProperties(indexedProperties);
            cronJob.setActive(!indexedProperties.isEmpty());

            modelService.save(cronJob);

            return Optional.of(cronJob);
        } catch (final SystemException e) {
            logReporter.logError(context, "Cannot create required objects for SOLR IndexedType " + indexedTypeKey + ".", e);

            return Optional.empty();
        }
    }

    protected IndexerOperationValues getIndexerOperationValues(final SystemSetupContext context) {
        final String indexerOperationValuesCode = configurationService.getConfiguration().getString("patches.groovy.solr.index.partial.cronJob.indexerType.queryType", IndexerOperationValues.PARTIAL_UPDATE.getCode());

        try {
            return IndexerOperationValues.valueOf(indexerOperationValuesCode.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException e) {
            logReporter.logWarn(context, "Incorrect value set for 'patches.groovy.solr.index.partial.cronJob.indexerType.queryType', '" + indexerOperationValuesCode + "' does not match any IndexerOperationValues enum value. Falling back to PARTIAL_UPDATE.");
            return IndexerOperationValues.PARTIAL_UPDATE;
        }
    }

    protected String computeIndexedTypeName(final SolrIndexedTypeModel indexedType) {
        final String indexName = indexedType.getIndexName();
        final String postfix = StringUtils.isEmpty(indexName)
                ? ""
                : "_" + indexName;

        return indexedType.getType().getCode() + postfix;
    }

    protected SolrIndexerQueryModel getSolrIndexerQuery(final SolrIndexedTypeModel solrIndexedType, final IndexerOperationValues indexerOperation) {
        final FlexibleSearchQuery fxsQuery = new FlexibleSearchQuery("SELECT {pk} FROM {SolrIndexerQuery} WHERE {solrIndexedType} = ?solrIndexedType and {type} = ?type");
        fxsQuery.addQueryParameter("solrIndexedType", solrIndexedType);
        fxsQuery.addQueryParameter("type", indexerOperation);

        return flexibleSearchService.searchUnique(fxsQuery);
    }

    protected SolrIndexedTypeModel getSolrIndexedType(final SolrIndexedTypeEnum indexedTypeKey) {
        final String indexedTypeName = configurationProvider.getSolrIndexedTypeName(indexedTypeKey);

        final FlexibleSearchQuery fxsQuery = new FlexibleSearchQuery("SELECT {pk} FROM {SolrIndexedType} WHERE {identifier} = ?identifier");
        fxsQuery.addQueryParameter("identifier", indexedTypeName);

        return flexibleSearchService.searchUnique(fxsQuery);
    }

    protected LanguageModel getSessionLanguage() {
        return commonI18NService.getLanguage(configurationService.getConfiguration().getString("patches.groovy.solr.index.partial.cronJob.language", "en"));
    }

    protected ServicelayerJobModel retrieveServiceLayerJob() {
        final String serviceLayerJobCode = configurationService.getConfiguration().getString("patches.groovy.solr.index.partial.serviceLayerJob.code", "patchesPartialSolrExtIndexerJob");

        final FlexibleSearchQuery fxsQuery = new FlexibleSearchQuery("SELECT {pk} FROM {ServicelayerJob} WHERE {code} = ?code");
        fxsQuery.addQueryParameter("code", serviceLayerJobCode);

        final List<ServicelayerJobModel> serviceLayerJobs = flexibleSearchService.<ServicelayerJobModel>search(fxsQuery).getResult();
        if (serviceLayerJobs.isEmpty()) {
            final ServicelayerJobModel servicelayerJob = modelService.create(ServicelayerJobModel.class);
            servicelayerJob.setCode(serviceLayerJobCode);
            servicelayerJob.setSpringId(configurationService.getConfiguration().getString("patches.groovy.solr.index.partial.serviceLayerJob.springId", "solrExtIndexerJob"));

            modelService.save(servicelayerJob);
            return servicelayerJob;
        }
        return serviceLayerJobs.get(0);
    }

}
