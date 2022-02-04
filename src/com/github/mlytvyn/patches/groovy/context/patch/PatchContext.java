package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext;
import com.github.mlytvyn.patches.groovy.context.ImpexContext;
import com.github.mlytvyn.patches.groovy.setup.GroovyPatchesSystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PatchContext<G extends GlobalContext> implements PatchContextDescriber, PatchContextDescriptor {

    protected final List<ImpexContext> impexContexts = new ArrayList<>();
    protected final List<ChangeFieldTypeContext> changeFieldTypeContexts = new ArrayList<>();
    protected final Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSyncedNow = new HashMap<>();

    protected final G globalContext;
    protected final ReleaseContext releaseContext;
    protected final String extensionName;
    protected final String number;
    protected final String id;

    protected PatchContext<G> nestedPatch;
    protected PatchContext<G> environmentPatch;

    protected String hash;
    protected String customPatchDataFolder;
    protected String description;
    protected List<String> impexes;
    protected EnumSet<EnvironmentEnum> environments = EnumSet.allOf(EnvironmentEnum.class);
    protected Consumer<SystemSetupContext> beforeConsumer;
    protected Consumer<SystemSetupContext> afterConsumer;

    public PatchContext(final G globalContext, final ReleaseContext releaseContext, final String extensionName, final String number, final String id) {
        this.globalContext = globalContext;
        this.releaseContext = releaseContext;
        this.extensionName = extensionName;
        this.number = number;
        this.id = id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PatchContext<G> patchContext = (PatchContext<G>) o;
        return hash().equals(patchContext.hash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash());
    }

    @Override
    public String toString() {
        return getNumber() + " | " + getId() + " | " + getDescription();
    }


    // May be used by legacy patches (not groovy based)
    @Override
    public PatchContextDescriber hash(final String hash) {
        this.hash = hash;
        return this;
    }

    @Override
    public PatchContextDescriber environment(final EnvironmentEnum... environments) {
        if (ArrayUtils.isNotEmpty(environments)) {
            this.environments = EnumSet.copyOf(Arrays.asList(environments));
        }
        return this;
    }

    @Override
    public PatchContextDescriber description(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public PatchContextDescriber before(final Consumer<SystemSetupContext> consumer) {
        if (isNotApplicable()) {
            return this;
        }

        this.beforeConsumer = consumer;
        return this;
    }

    @Override
    public PatchContextDescriber after(final Consumer<SystemSetupContext> consumer) {
        if (isNotApplicable()) {
            return this;
        }

        this.afterConsumer = consumer;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PatchContextDescriber nested(final PatchContextDescriber nested) {
        if (isNotApplicable() || !PatchContext.class.isAssignableFrom(nested.getClass())) {
            return this;
        }

        this.nestedPatch = (PatchContext<G>) nested;
        return this;
    }

    @Override
    public PatchContextDescriber customPatchDataFolder(final String customPatchDataFolder) {
        if (isNotApplicable()) {
            return this;
        }

        this.customPatchDataFolder = customPatchDataFolder;
        return this;
    }

    @Override
    public PatchContextDescriber withImpexes(final String... impexes) {
        if (isNotApplicable()) {
            return this;
        }

        this.impexes = ArrayUtils.isEmpty(impexes) ? Collections.emptyList() : Arrays.asList(impexes);
        return this;
    }

    @Override
    public PatchContextDescriber importEmailTemplates(final EmailTemplateEnum... emailTemplates) {
        if (isNotApplicable()) {
            return this;
        }

        globalContext.importEmailTemplates(emailTemplates);
        return this;
    }

    @Override
    public PatchContextDescriber importEmailComponentTemplates(final EnumSet<SiteEnum> sites, final EnumSet<EmailComponentTemplateEnum> emailComponentTemplates) {
        if (isNotApplicable()) {
            return this;
        }

        globalContext.importEmailComponentTemplates(sites, emailComponentTemplates);
        return this;
    }

    @Override
    public PatchContextDescriber importEmailComponentTemplates(final EmailComponentTemplateEnum... emailComponentTemplates) {
        if (isNotApplicable()) {
            return this;
        }

        globalContext.importEmailComponentTemplates(emailComponentTemplates);
        return this;
    }

    @Override
    public PatchContextDescriber syncContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (isNotApplicable()) {
            return this;
        }

        releaseContext.syncContentCatalogs(contentCatalogs);
        return this;
    }

    @Override
    public PatchContextDescriber forcedSyncContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (isNotApplicable()) {
            return this;
        }

        releaseContext.forcedSyncContentCatalogs(contentCatalogs);
        return this;
    }

    @Override
    public PatchContextDescriber removeContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (isNotApplicable()) {
            return this;
        }

        releaseContext.removeContentCatalogs(contentCatalogs);
        return this;
    }

    @Override
    public PatchContextDescriber syncContentCatalogsNow(final ContentCatalogEnum... contentCatalogs) {
        if (isNotApplicable()) {
            return this;
        }

        Arrays.stream(contentCatalogs)
            .forEach(contentCatalog -> contentCatalogsToBeSyncedNow.putIfAbsent(contentCatalog, false));
        return this;
    }

    @Override
    public PatchContextDescriber forcedSyncContentCatalogsNow(final ContentCatalogEnum... contentCatalogs) {
        if (isNotApplicable()) {
            return this;
        }

        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            Arrays.stream(contentCatalogs)
                .forEach(contentCatalog -> contentCatalogsToBeSyncedNow.put(contentCatalog, true));
        }
        return this;
    }

    @Override
    public PatchContextDescriber withImpexContexts(final ImpexContext... impexContexts) {
        if (isNotApplicable()) {
            return this;
        }

        if (ArrayUtils.isNotEmpty(impexContexts)) {
            withImpexContexts(Arrays.asList(impexContexts));
        }
        return this;
    }

    @Override
    public PatchContextDescriber withImpexContexts(final List<ImpexContext> impexContexts) {
        if (isNotApplicable()) {
            return this;
        }

        if (CollectionUtils.isNotEmpty(impexContexts)) {
            this.impexContexts.addAll(impexContexts);
        }
        return this;
    }

    @Override
    public PatchContextDescriber changeFieldType(final ChangeFieldTypeContext... changeFieldTypeContexts) {
        if (isNotApplicable()) {
            return this;
        }

        if (ArrayUtils.isNotEmpty(changeFieldTypeContexts)) {
            this.changeFieldTypeContexts.addAll(Arrays.asList(changeFieldTypeContexts));
        }
        return this;
    }

    @Override
    public PatchContextDescriber removeOrphanedTypes() {
        if (isNotApplicable()) {
            return this;
        }

        globalContext.removeOrphanedTypes();
        return this;
    }

    @Override
    public PatchContextDescriber schedulePartialUpdate(final SolrEnum solrIndex, final Set<String> indexedProperties) {
        if (isNotApplicable()) {
            return this;
        }

        globalContext.schedulePartialUpdate(solrIndex, indexedProperties);
        return this;
    }

    @Override
    public PatchContextDescriber fullReIndex(final SolrEnum... solrIndexes) {
        if (isNotApplicable()) {
            return this;
        }

        if (ArrayUtils.isNotEmpty(solrIndexes)) {
            globalContext.fullReIndex(Arrays.asList(solrIndexes));
        }
        return this;
    }

    @Override
    public PatchContextDescriber removeSolrCore(final SolrEnum... solrIndexes) {
        if (isNotApplicable()) {
            return this;
        }

        if (ArrayUtils.isNotEmpty(solrIndexes)) {
            globalContext.removeSolrCores(Arrays.asList(solrIndexes));
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PatchContextDescriber environmentPatch(final EnumSet<EnvironmentEnum> environments, final Supplier<PatchContextDescriber> supplier) {
        if (isNotApplicable()) {
            return this;
        }

        final PatchContextDescriber environmentPatch = supplier.get();

        if (environments.contains(getCurrentEnvironment()) && PatchContext.class.isAssignableFrom(environmentPatch.getClass())) {
            this.environmentPatch = (PatchContext<G>) environmentPatch;
            this.environmentPatch.environments = EnumSet.of(getCurrentEnvironment());
        }
        return this;
    }

    @Override
    public String hash() {
        return Optional.ofNullable(hash)
            .orElseGet(() -> {
                final String releaseId = releaseContext.id();
                // almost copy-paste from SystemSetupCollectorResult
                // already applied patches will use original hash value retrieved from upper env
                final String key = extensionName + "-" + releaseId + "-" + number;
                return GroovyPatchesSystemSetup.MD5.hashBytes(key.getBytes()).toString();
            });
    }

    @Override
    public PatchContextDescriber createRelatedPatch() {
        return new PatchContext<>(globalContext, releaseContext, extensionName, number, id);
    }

    @Override
    public String getName() {
        return getReleaseContext().id() + " | " + getNumber() + " | " + getId();
    }

    @Override
    public EnvironmentEnum getCurrentEnvironment() {
        return globalContext.getCurrentEnvironment();
    }

    @Override
    public List<ChangeFieldTypeContext> getChangeFieldTypeContexts() {
        return Collections.unmodifiableList(changeFieldTypeContexts);
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public List<ImpexContext> getImpexContexts() {
        return Collections.unmodifiableList(impexContexts);
    }

    @Override
    public List<String> getImpexes() {
        return Optional.ofNullable(impexes)
            .map(Collections::unmodifiableList)
            .orElse(null);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Map<ContentCatalogEnum, Boolean> getContentCatalogsToBeSyncedNow() {
        return contentCatalogsToBeSyncedNow;
    }

    @Override
    public Set<EnvironmentEnum> getEnvironments() {
        return Collections.unmodifiableSet(environments);
    }

    @Override
    public Optional<Consumer<SystemSetupContext>> getBeforeConsumer() {
        return Optional.ofNullable(beforeConsumer);
    }

    @Override
    public Optional<Consumer<SystemSetupContext>> getAfterConsumer() {
        return Optional.ofNullable(afterConsumer);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Optional<PatchContextDescriptor> getNestedPatch() {
        return Optional.ofNullable(nestedPatch);
    }

    @Override
    public ReleaseContext getReleaseContext() {
        return releaseContext;
    }

    @Override
    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    @Override
    public boolean isApplicable() {
        return environments.contains(getCurrentEnvironment());
    }

    @Override
    public boolean isNotApplicable() {
        return !isApplicable();
    }

    @Override
    public Optional<PatchContextDescriptor> getEnvironmentPatch() {
        return Optional.ofNullable(environmentPatch);
    }

    @Override
    public String getPatchDataFolder() {
        return Optional.ofNullable(customPatchDataFolder)
            .orElseGet(this::getId);
    }
}
