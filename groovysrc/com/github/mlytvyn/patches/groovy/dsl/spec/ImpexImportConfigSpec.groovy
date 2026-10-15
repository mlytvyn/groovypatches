package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class ImpexImportConfigSpec implements ImpexConfigFlags {
    private final PatchContextDescriber context
    private final ImpexImportConfig importConfig

    ImpexImportConfigSpec(PatchContextDescriber context, ImpexImportConfig importConfig) {
        this.context = context
        this.importConfig = importConfig
    }

    @Override
    void applyFlag(String flag, boolean value) { importConfig."$flag"(value) }
}
