package com.github.mlytvyn.patches.groovy.context.impex;

import java.io.Serial;
import java.io.Serializable;

public class ImpexImportConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -609475195699174983L;

    private boolean legacyMode;
    private boolean failOnError;
    private boolean errorIfMissing;
    private boolean removeOnSuccess;
    private boolean synchronous;
    private boolean enableCodeExecution = true;

    private ImpexImportConfig() {
    }

    public static ImpexImportConfig create() {
        return new ImpexImportConfig();
    }

    public boolean legacyMode() {
        return legacyMode;
    }

    public ImpexImportConfig legacyMode(final boolean legacyMode) {
        this.legacyMode = legacyMode;
        return this;
    }

    public boolean failOnError() {
        return failOnError;
    }

    public ImpexImportConfig failOnError(final boolean failOnError) {
        this.failOnError = failOnError;
        return this;
    }

    public boolean errorIfMissing() {
        return errorIfMissing;
    }

    public ImpexImportConfig errorIfMissing(final boolean errorIfMissing) {
        this.errorIfMissing = errorIfMissing;
        return this;
    }

    public boolean removeOnSuccess() {
        return removeOnSuccess;
    }

    public ImpexImportConfig removeOnSuccess(final boolean removeOnSuccess) {
        this.removeOnSuccess = removeOnSuccess;
        return this;
    }

    public boolean synchronous() {
        return synchronous;
    }

    public ImpexImportConfig synchronous(final boolean synchronous) {
        this.synchronous = synchronous;
        return this;
    }

    public boolean enableCodeExecution() {
        return enableCodeExecution;
    }

    public ImpexImportConfig enableCodeExecution(final boolean enableCodeExecution) {
        this.enableCodeExecution = enableCodeExecution;
        return this;
    }

    @Override
    public String toString() {
        return "ImpexImportConfig{" +
                "legacyMode=" + legacyMode +
                ", failOnError=" + failOnError +
                ", errorIfMissing=" + errorIfMissing +
                ", removeOnSuccess=" + removeOnSuccess +
                ", synchronous=" + synchronous +
                ", enableCodeExecution=" + enableCodeExecution +
                '}';
    }
}
