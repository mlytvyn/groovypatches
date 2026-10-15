package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext

class ImpexSpec {

    private final ImpexContext impexContext

    ImpexSpec(ImpexContext impexContext) {
        this.impexContext = impexContext
    }

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
}
