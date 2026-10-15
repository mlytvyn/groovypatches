package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext

class ImpexSpec implements ImpexConfigFlags {

    private final String name
    private boolean fqn = false
    private final List<Closure> configurators = []

    ImpexSpec(String name) {
        this.name = name
    }

    void fqn(boolean fqn) { this.fqn = fqn }

    @Override
    void applyFlag(String flag, boolean value) { configurators << { ImpexContext ctx -> ctx."$flag"(value) } }

    void templateContext(
            String description,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexTemplateContextSpec) Closure closure = {}
    ) {
        def templateContext = ImpexTemplateContextSpec.build(description, closure)
        configurators << { ImpexContext ctx -> ctx.addTemplateContext(templateContext) }
    }

    ImpexContext build() {
        def impexContext = fqn ? ImpexContext.fqn(name) : ImpexContext.of(name)
        configurators.each { it(impexContext) }
        impexContext
    }
}
