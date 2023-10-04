package com.github.mlytvyn.patches.groovy.solrfacetsearch.daos.impl;

import com.github.mlytvyn.patches.groovy.solrfacetsearch.daos.PatchesSolrFacetSearchConfigDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.daos.impl.DefaultSolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;

import java.util.List;

public class DefaultPatchesSolrFacetSearchConfigDao extends DefaultSolrFacetSearchConfigDao implements PatchesSolrFacetSearchConfigDao {

    private static final String FIND_CONFIGS_FOR_REINDEX_AFTER_STARTUP = "SELECT {pk}" +
            " FROM {" + SolrFacetSearchConfigModel._TYPECODE + "}" +
            " WHERE {" + SolrFacetSearchConfigModel.REINDEXAFTERSTARTUP + "} = ?" + SolrFacetSearchConfigModel.REINDEXAFTERSTARTUP;

    @Override
    public List<SolrFacetSearchConfigModel> findAllSolrFacetSearchConfigsToReindex() {
        final FlexibleSearchQuery fxsQuery = new FlexibleSearchQuery(FIND_CONFIGS_FOR_REINDEX_AFTER_STARTUP);
        fxsQuery.addQueryParameter(SolrFacetSearchConfigModel.REINDEXAFTERSTARTUP, true);
        return getFlexibleSearchService().<SolrFacetSearchConfigModel>search(fxsQuery).getResult();
    }
}
