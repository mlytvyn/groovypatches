package com.github.mlytvyn.patches.groovy.context.impex;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ImpexContext {

    private final String name;
    private ImpexImportConfig config;
    private Collection<ImpexTemplateContext> templateContexts;
    private final boolean fqn;

    private ImpexContext(final String name, final boolean fqn) {
        this.name = name;
        this.fqn = fqn;
    }

    public static ImpexContext of(final String name) {
        return new ImpexContext(name, false);
    }

    public static ImpexContext fqn(final String name) {
        return new ImpexContext(name, true);
    }

    @Nullable
    public Collection<ImpexTemplateContext> templateContexts() {
        return this.templateContexts;
    }

    /**
     * Corresponding ImpEx file will be executed for each passed Template Content
     *
     * @param templateContext Template Contexts
     * @return this
     */
    public ImpexContext addTemplateContext(final ImpexTemplateContext templateContext) {
        if (this.templateContexts == null) {
            this.templateContexts = new ArrayList<>();
        }
        this.templateContexts.add(templateContext);

        return this;
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
                    final ImpexImportConfig config = ImpexImportConfig.create();
                    config(config);
                    return config;
                });
    }

    public String name() {
        return name;
    }

    public boolean isFqn() {
        return fqn;
    }

    public ImpexContext config(final ImpexImportConfig config) {
        this.config = config;
        return this;
    }

    @Override
    public String toString() {
        return "ImpexContext{" +
                "name='" + name + '\'' +
                ", config=" + config +
                ", fqn=" + fqn +
                '}';
    }

}
