package com.github.mlytvyn.patches.groovy.dsl


import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.dsl.spec.PatchSpec

class PatchDsl {

    static registerPatch(
            PatchContextDescriber context,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchSpec) Closure closure
    ) {
        registerPatch(new PatchSpec(context), closure)
    }

    static registerPatch(
            PatchSpec spec,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = spec
        closure()
    }
}
