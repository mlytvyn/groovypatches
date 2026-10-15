package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.SolrEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class SolrSpec {

    private final PatchContextDescriber context

    SolrSpec(PatchContextDescriber context) {
        this.context = context
    }

    void fullReIndex(SolrEnum... solrIndexes) { context.fullReIndex(solrIndexes) }

    void partialReIndex(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PartialReIndexSpec) Closure closure
    ) {
        def spec = new PartialReIndexSpec()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = spec
        closure()
        context.partialReIndex(spec.indexedType(), spec.indexedProperties() as String[])
    }

    void removeCore(SolrEnum... solrIndexes) { context.removeSolrCore(solrIndexes) }
}
