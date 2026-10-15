package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext

class ImpexTemplateContextSpec {

    private final ImpexTemplateContext templateContext

    ImpexTemplateContextSpec(ImpexTemplateContext templateContext) {
        this.templateContext = templateContext
    }

    static ImpexTemplateContext build(
            String description,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexTemplateContextSpec) Closure closure
    ) {
        def templateContext = ImpexTemplateContext.of(description)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new ImpexTemplateContextSpec(templateContext)
        closure()
        templateContext
    }

    void macroParameter(String name, Object value) { templateContext.macroParameter(name, value) }
}
