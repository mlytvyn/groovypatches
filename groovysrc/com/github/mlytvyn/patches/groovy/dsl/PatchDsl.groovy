package com.github.mlytvyn.patches.groovy.dsl


import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.dsl.spec.PatchSpec
import groovy.transform.TypeChecked

class PatchDsl {

    static registerPatch(
            PatchContextDescriber context,
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = PatchSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = new PatchSpec(context)
        closure()
    }
}

