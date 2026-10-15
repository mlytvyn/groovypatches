package com.github.mlytvyn.patches.groovy.dsl.spec

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
        def spec = new ImpexSpec(name)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = spec
        closure()
        context.withImpexes(spec.build())
    }
}

