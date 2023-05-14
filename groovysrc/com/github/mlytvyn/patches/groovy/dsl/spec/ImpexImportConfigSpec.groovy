package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class ImpexImportConfigSpec {
    private final PatchContextDescriber context
    private final ImpexImportConfig importConfig

    ImpexImportConfigSpec(PatchContextDescriber context, ImpexImportConfig importConfig) {
        this.context = context
        this.importConfig = importConfig
    }

    void legacyMode(boolean enabled) { importConfig.legacyMode(enabled) }

    void failOnError(boolean enabled) { importConfig.failOnError(enabled) }

    void errorIfMissing(boolean enabled) { importConfig.errorIfMissing(enabled) }

    void removeOnSuccess(boolean enabled) { importConfig.removeOnSuccess(enabled) }

    void synchronous(boolean enabled) { importConfig.synchronous(enabled) }

    void enableCodeExecution(boolean enabled) { importConfig.enableCodeExecution(enabled) }
}