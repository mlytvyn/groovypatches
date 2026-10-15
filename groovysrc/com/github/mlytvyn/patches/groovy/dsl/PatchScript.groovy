package com.github.mlytvyn.patches.groovy.dsl

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.context.patch.PatchesCollector
import com.github.mlytvyn.patches.groovy.dsl.spec.PatchSpec

abstract class PatchScript extends Script {

    void patch(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchSpec) Closure closure
    ) {
        PatchDsl.registerPatch(createSpec(patchContext), closure)
    }

    protected PatchContextDescriber getPatchContext() {
        binding.getVariable(PatchesCollector.PATCH_CONTEXT_PARAM) as PatchContextDescriber
    }

    protected PatchSpec createSpec(PatchContextDescriber context) { new PatchSpec(context) }
}
