package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext

class ImpexSpec {

    private final ImpexContext impexContext
    private boolean fqn = false

    ImpexSpec(String name) {
        this.impexContext = ImpexContext.of(name)
    }

    void fqn(boolean fqn) { this.fqn = fqn }

    void legacyMode(boolean enabled) { impexContext.legacyMode(enabled) }

    void failOnError(boolean enabled) { impexContext.failOnError(enabled) }

    void errorIfMissing(boolean enabled) { impexContext.errorIfMissing(enabled) }

    void removeOnSuccess(boolean enabled) { impexContext.removeOnSuccess(enabled) }

    void synchronous(boolean enabled) { impexContext.synchronous(enabled) }

    void enableCodeExecution(boolean enabled) { impexContext.enableCodeExecution(enabled) }

    void templateContext(
            String description,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexTemplateContextSpec) Closure closure = {}
    ) {
        impexContext.addTemplateContext(ImpexTemplateContextSpec.build(description, closure))
    }

    ImpexContext build() {
        if (!fqn) {
            return impexContext
        }

        // fqn-ness is fixed at ImpexContext construction time, rebuild and copy over whatever was already configured
        def fqnContext = ImpexContext.fqn(impexContext.name())
        impexContext.config().ifPresent { fqnContext.config(it) }
        impexContext.templateContexts()?.each { fqnContext.addTemplateContext(it) }
        fqnContext
    }
}
