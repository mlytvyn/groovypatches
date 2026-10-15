package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class CatalogsSpec {

    private final PatchContextDescriber context

    CatalogsSpec(PatchContextDescriber context) {
        this.context = context
    }

    void content(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ContentCatalogsSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new ContentCatalogsSpec(context)
        closure()
    }

    void product(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ProductCatalogsSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new ProductCatalogsSpec(context)
        closure()
    }
}
