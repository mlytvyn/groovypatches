package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext

class ImpexSpec {

    private final String name
    private boolean fqn = false
    private final List<Closure> configurators = []

    ImpexSpec(String name) {
        this.name = name
    }

    void fqn(boolean fqn) { this.fqn = fqn }

    void legacyMode(boolean enabled) { configurators << { ImpexContext ctx -> ctx.legacyMode(enabled) } }

    void failOnError(boolean enabled) { configurators << { ImpexContext ctx -> ctx.failOnError(enabled) } }

    void errorIfMissing(boolean enabled) { configurators << { ImpexContext ctx -> ctx.errorIfMissing(enabled) } }

    void removeOnSuccess(boolean enabled) { configurators << { ImpexContext ctx -> ctx.removeOnSuccess(enabled) } }

    void synchronous(boolean enabled) { configurators << { ImpexContext ctx -> ctx.synchronous(enabled) } }

    void enableCodeExecution(boolean enabled) { configurators << { ImpexContext ctx -> ctx.enableCodeExecution(enabled) } }

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
