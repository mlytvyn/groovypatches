package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum

class PartialReIndexSpec {

    private SolrIndexedTypeEnum indexedType
    private final List<String> indexedProperties = []

    void type(SolrIndexedTypeEnum indexedType) { this.indexedType = indexedType }

    void properties(String... indexedProperties) { this.indexedProperties.addAll(indexedProperties) }

    SolrIndexedTypeEnum indexedType() { indexedType }

    List<String> indexedProperties() { indexedProperties }
}
