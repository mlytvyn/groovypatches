package com.github.mlytvyn.patches.groovy.dsl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber;
import groovy.lang.Closure;

public interface PatchDslRegistrar {

    /**
     * This method will create the closure which is bound to the Patch script as the {@link com.github.mlytvyn.patches.groovy.context.patch.PatchesCollector#PATCH_PARAM} variable,
     * so that a patch can be declared as a {@code patch { ... }} block.
     *
     * @param context patch context of the Patch being evaluated
     * @return DSL entry point closure
     */
    Closure<?> createPatchClosure(PatchContextDescriber context);
}
