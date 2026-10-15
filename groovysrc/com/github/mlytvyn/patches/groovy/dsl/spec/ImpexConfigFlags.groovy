package com.github.mlytvyn.patches.groovy.dsl.spec

trait ImpexConfigFlags {

    abstract void applyFlag(String flag, boolean value)

    void legacyMode(boolean enabled) { applyFlag('legacyMode', enabled) }

    void failOnError(boolean enabled) { applyFlag('failOnError', enabled) }

    void errorIfMissing(boolean enabled) { applyFlag('errorIfMissing', enabled) }

    void removeOnSuccess(boolean enabled) { applyFlag('removeOnSuccess', enabled) }

    void synchronous(boolean enabled) { applyFlag('synchronous', enabled) }

    void enableCodeExecution(boolean enabled) { applyFlag('enableCodeExecution', enabled) }
}
