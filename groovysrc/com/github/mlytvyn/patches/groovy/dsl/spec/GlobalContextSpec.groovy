package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext

class GlobalContextSpec {
    private GlobalContext context

    void removeOrphanedTypes(boolean flag) { context.removeOrphanedTypes(flag) }
}

