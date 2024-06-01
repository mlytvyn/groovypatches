package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalPatchesException;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import com.github.mlytvyn.patches.groovy.util.ParallelPoolExecutor;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

public class GlobalContextSolrRemoveCoreAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "groovyPatchesParallelPoolExecutor")
    private ParallelPoolExecutor parallelPoolExecutor;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "facetSearchConfigService")
    private FacetSearchConfigService facetSearchConfigService;
    @Resource(name = "solrIndexService")
    private SolrIndexService solrIndexService;
    @Resource(name = "solrSearchProviderFactory")
    private SolrSearchProviderFactory solrSearchProviderFactory;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        final Set<SolrEnum> solrCoresForRemoval = globalContext.solrCoresForRemoval();
        if (solrCoresForRemoval.isEmpty()) return;

        logReporter.logInfo(context, "[Global] Started SOLR core's removal");

        parallelPoolExecutor.execute(context).accept(() -> solrCoresForRemoval.parallelStream()
                .forEach(solrIndexKey -> executeSolrCoreRemove(context, globalContext, configurationProvider.getSolrCoreName(solrIndexKey))));

        logReporter.logInfo(context, "[Global] Completed SOLR core's removal");
    }

    protected void executeSolrCoreRemove(final SystemSetupContext context, final GlobalContext globalContext, final String solrCoreName) {
        try {
            final FacetSearchConfig facetSearchConfig = facetSearchConfigService.getConfiguration(solrCoreName);
            for (final IndexedType indexedType : facetSearchConfig.getIndexConfig().getIndexedTypes().values()) {
                final List<SolrIndexModel> indexes = solrIndexService.getIndexesForConfigAndType(facetSearchConfig.getName(), indexedType.getIdentifier());
                for (final SolrIndexModel index : indexes) {
                    final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
                    final Index solrIndex = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, index.getQualifier());

                    solrSearchProvider.deleteIndex(solrIndex);
                    logReporter.logInfo(context, String.format("Solr core %s has been removed.", solrIndex.getName()));
                }
            }
        } catch (final FacetConfigServiceException e) {
            throw new GlobalPatchesException(globalContext, String.format("Error during searching for facetSearchConfig for index %s", solrCoreName), e);
        } catch (final SolrServiceException e) {
            logReporter.logError(context, String.format("Error during searching for facetSearchConfig for index %s", solrCoreName), e);
        } catch (final UnknownIdentifierException e) {
            logReporter.logInfo(context, String.format("Solr index core is not created yet %s", solrCoreName), "red");
        }
    }

}
