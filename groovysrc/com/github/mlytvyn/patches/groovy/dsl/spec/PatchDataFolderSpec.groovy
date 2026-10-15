package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.context.patch.PatchDataFolderRelation

import java.nio.file.Path
import java.nio.file.Paths

class PatchDataFolderSpec {

    private final PatchContextDescriber context

    PatchDataFolderSpec(PatchContextDescriber context) {
        this.context = context
    }

    void release(String folder) { release(Paths.get(folder)) }

    void release(Path folder) { context.customPatchDataFolder(folder, PatchDataFolderRelation.RELEASE) }

    void root(String folder) { root(Paths.get(folder)) }

    void root(Path folder) { context.customPatchDataFolder(folder, PatchDataFolderRelation.ROOT) }
}
