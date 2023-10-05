package com.github.mlytvyn.patches.groovy.solrfacetsearch.config.impl;

import com.github.mlytvyn.patches.groovy.solrfacetsearch.config.PatchesFacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.impl.DefaultFacetSearchConfigService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class DefaultPatchesFacetSearchConfigService extends DefaultFacetSearchConfigService implements PatchesFacetSearchConfigService {

    @Override
    public Collection<String> findUnresoledIndexedProperties(final IndexedType indexedType, final Collection<String> indexedPropertyNames) {
        final Set<String> resolvableProperties = resolveIndexedPropertiesInIndexedTypeTree(indexedType, indexedPropertyNames).keySet();

        return indexedPropertyNames.stream()
                .filter(not(resolvableProperties::contains))
                .collect(Collectors.toSet());
    }

    private Map<String, IndexedProperty> resolveIndexedPropertiesInIndexedTypeTree(final IndexedType indexedType, final Collection<String> indexedPropertyNames) {
        if (CollectionUtils.isEmpty(indexedPropertyNames)) return Collections.emptyMap();

        final Map<String, IndexedProperty> indexedProperties = indexedType.getIndexedProperties();
        if (MapUtils.isEmpty(indexedProperties)) return Collections.emptyMap();

        return indexedPropertyNames.stream()
                .map(indexedProperties::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(IndexedProperty::getName, Function.identity()));
    }
}
