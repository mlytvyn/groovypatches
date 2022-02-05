package com.github.mlytvyn.patches.groovy.context.impex;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Optional;

@Data
@Accessors(chain = true, fluent = true)
@RequiredArgsConstructor
public class ImpexContext {

    @NonNull
    private final String name;
    private ImpexImportConfig config;

    public static ImpexContext of(final String name) {
        return new ImpexContext(name);
    }

    public ImpexContext legacyMode(final boolean legacyMode) {
        getConfig().legacyMode(legacyMode);
        return this;
    }

    public ImpexContext enableCodeExecution(final boolean enableCodeExecution) {
        getConfig().enableCodeExecution(enableCodeExecution);
        return this;
    }

    public ImpexContext failOnError(final boolean failOnError) {
        getConfig().failOnError(failOnError);
        return this;
    }

    public ImpexContext removeOnSuccess(final boolean removeOnSuccess) {
        getConfig().removeOnSuccess(removeOnSuccess);
        return this;
    }

    public ImpexContext synchronous(final boolean synchronous) {
        getConfig().synchronous(synchronous);
        return this;
    }

    public ImpexContext errorIfMissing(final boolean errorIfMissing) {
        getConfig().errorIfMissing(errorIfMissing);
        return this;
    }

    public Optional<ImpexImportConfig> config() {
        return Optional.ofNullable(config);
    }

    private ImpexImportConfig getConfig() {
        return config()
                .orElseGet(() -> {
                    final ImpexImportConfig config = ImpexImportConfig.builder().build();
                    config(config);
                    return config;
                });
    }

}
