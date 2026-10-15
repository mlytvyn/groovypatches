package com.github.mlytvyn.patches.groovy.dsl

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.dsl.spec.PatchSpec

class DefaultPatchDslRegistrar implements PatchDslRegistrar {

    @Override
    Closure<?> createPatchClosure(PatchContextDescriber context) {
        return { Closure closure -> PatchDsl.registerPatch(createSpec(context), closure) }
    }

    protected PatchSpec createSpec(PatchContextDescriber context) { new PatchSpec(context) }
}
