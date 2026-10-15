package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class ImpexesSpec {

    private final PatchContextDescriber context
    private ImpexImportConfig importConfig

    ImpexesSpec(PatchContextDescriber context) {
        this.context = context
    }

    void config(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexImportConfigSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST

        if (importConfig == null) {
            importConfig = ImpexImportConfig.create()
            context.impexImportConfig(importConfig)
        }

        closure.delegate = new ImpexImportConfigSpec(context, importConfig)
        closure()
    }

    void all() { context.withImpexes() }

    void impex(
            String name,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexSpec) Closure closure = {}
    ) {
        registerImpex(ImpexContext.of(name), closure)
    }

    void fqnImpex(
            String name,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexSpec) Closure closure = {}
    ) {
        registerImpex(ImpexContext.fqn(name), closure)
    }

    private void registerImpex(ImpexContext impexContext, Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new ImpexSpec(impexContext)
        closure()
        context.withImpexes(impexContext)
    }
}

