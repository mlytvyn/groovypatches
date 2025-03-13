package com.github.mlytvyn.patches.groovy.solrfacetsearch.daos;

import de.hybris.platform.solrfacetsearch.daos.SolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

import java.util.List;

public interface PatchesSolrFacetSearchConfigDao extends SolrFacetSearchConfigDao {

    /**
     * This method returns all solrFacetSearchConfig to be re-indexed, according to current deployment patches.
     *
     * @return solrFacetSearchConfig
     */
    List<SolrFacetSearchConfigModel> findAllSolrFacetSearchConfigsToReindex();
}
