package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.context.CurrentEnvironmentProvider;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.daos.SolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalContextSolrFullReIndexAction implements GlobalContextAction<GlobalContext> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;
    @Resource(name = "modelService")
    private ModelService modelService;
    @Resource(name = "currentEnvironmentProvider")
    private CurrentEnvironmentProvider currentEnvironmentProvider;
    @Resource(name = "solrFacetSearchConfigDao")
    private SolrFacetSearchConfigDao solrFacetSearchConfigDao;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        final Set<SolrEnum> solrCoresForReIndex = globalContext.solrCoresForFullReIndex();
        if (solrCoresForReIndex.isEmpty()) return;

        logReporter.logInfo(context, "[Global] Started SOLR request for full reindex");

        final List<SolrFacetSearchConfigModel> facetConfigs = solrCoresForReIndex.stream()
                .map(solrIndexKey -> configurationProvider.getSolrCoreName(solrIndexKey))
                .map(configName -> markSolrFacetSearchConfigForReIndex(context, configName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (!facetConfigs.isEmpty()) {
            modelService.saveAll(facetConfigs);
        }

        logReporter.logInfo(context, "[Global] Completed SOLR request for full reindex");
    }


    protected Optional<SolrFacetSearchConfigModel> markSolrFacetSearchConfigForReIndex(final SystemSetupContext context, final String configName) {
        try {
            final SolrFacetSearchConfigModel config = solrFacetSearchConfigDao.findFacetSearchConfigByName(configName);

            // this flag will be used after all patches to identify what SOLR Index should be re-indexed
            config.setReIndexAfterStartUp(true);

            if (currentEnvironmentProvider.getCurrentEnvironment() == EnvironmentEnum.LOCAL) {
                logReporter.logInfo(context, "Full reindex for [" + configName + "] will be executed after system update", "blue");
            } else {
                logReporter.logInfo(context, "Full reindex for [" + configName + "] will be executed during hybris startup", "blue");
            }
            return Optional.of(config);
        } catch (final UnknownIdentifierException e) {
            logReporter.logInfo(context, String.format("Solr index core is not yet created %s", configName), "orange");
            return Optional.empty();
        }
    }

}
