package com.github.mlytvyn.patches.groovy.solrfacetsearch.config;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import java.util.Collection;

public interface PatchesFacetSearchConfigService extends FacetSearchConfigService {

    /**
     * Find which of {@code indexedPropertyNames} cannot be resolved in given indexed type.
     *
     * @param indexedType          the indexed type
     * @param indexedPropertyNames the collection of indexed property names
     * @return collection of property names that couldn't be resolved
     */
    Collection<String> findUnresoledIndexedProperties(IndexedType indexedType, Collection<String> indexedPropertyNames);
}
