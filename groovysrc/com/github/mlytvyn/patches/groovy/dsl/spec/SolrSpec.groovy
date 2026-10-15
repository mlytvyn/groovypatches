package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.SolrEnum
import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class SolrSpec {

    private final PatchContextDescriber context

    SolrSpec(PatchContextDescriber context) {
        this.context = context
    }

    void fullReIndex(SolrEnum... solrIndexes) { context.fullReIndex(solrIndexes) }

    void partialReIndex(SolrIndexedTypeEnum indexedType, String... indexedProperties) {
        context.partialReIndex(indexedType, indexedProperties)
    }

    void removeCore(SolrEnum... solrIndexes) { context.removeSolrCore(solrIndexes) }
}
