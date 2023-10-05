package com.github.mlytvyn.patches.groovy.setup;

import de.hybris.platform.commerceservices.setup.SetupSolrIndexerService;

public interface PatchesSetupSolrIndexerService extends SetupSolrIndexerService {

    void executeSolrIndexerCronJob(String solrFacetSearchConfigName, boolean fullReIndex, boolean synchronous);
}
