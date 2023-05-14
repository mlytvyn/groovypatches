package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum
import com.github.mlytvyn.patches.groovy.EnvironmentEnum
import com.github.mlytvyn.patches.groovy.SiteEnum
import com.github.mlytvyn.patches.groovy.SolrEnum
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig
import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.context.patch.PatchDataFolderRelation
import de.hybris.platform.core.initialization.SystemSetupContext

import java.nio.file.Path
import java.util.function.Consumer
import java.util.function.Supplier

class PatchSpec {

    private final PatchContextDescriber context

    PatchSpec(PatchContextDescriber context) {
        this.context = context
    }

    void hash(String hash) { context.hash(hash) }

    void environment(EnvironmentEnum... environments) { context.environment(environments) }

    void description(String description) { context.description(description) }

    void impexes(
            @DelegatesTo(strategy = Closure.DELEGATE_ONLY, value = ImpexesSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_ONLY
        closure.delegate = new ImpexesSpec(context)
        closure()
    }


    /*








     */

    void before(Consumer<SystemSetupContext> consumer) { context.before(consumer) }

    void after(Consumer<SystemSetupContext> consumer) { context.after(consumer) }

    void withNestedPatch(PatchContextDescriber nested) { context.withNestedPatch(nested) }

    void customPatchDataFolder(Path customPatchDataFolder) { context.customPatchDataFolder(customPatchDataFolder) }

    void customPatchDataFolder(Path customPatchDataFolder, PatchDataFolderRelation relation) {
        context.customPatchDataFolder(customPatchDataFolder, relation)
    }

//    void impexImportConfig(ImpexImportConfig config) { context.impexImportConfig(config) }

    void withImpexes(String... impexes) { context.withImpexes(impexes) }

    void withImpexes(ImpexContext... impexContexts) { context.withImpexes(impexContexts) }

    void importEmailTemplates(EmailTemplateEnum... emailTemplates) { context.importEmailTemplates(emailTemplates) }

    void importEmailComponentTemplates(EnumSet<SiteEnum> sites, EmailComponentTemplateEnum... emailComponentTemplates) {
        context.importEmailComponentTemplates(sites, emailComponentTemplates)
    }

    void importEmailComponentTemplates(EmailComponentTemplateEnum... emailComponentTemplates) {
        context.importEmailComponentTemplates(emailComponentTemplates)
    }

    void syncContentCatalogs(ContentCatalogEnum... contentCatalogs) { context.syncContentCatalogs(contentCatalogs) }

    void forcedSyncContentCatalogs(ContentCatalogEnum... contentCatalogs) {
        context.forcedSyncContentCatalogs(contentCatalogs)
    }

    void removeContentCatalogs(ContentCatalogEnum... contentCatalogs) { context.removeContentCatalogs(contentCatalogs) }

    void syncContentCatalogsNow(ContentCatalogEnum... contentCatalogs) {
        context.syncContentCatalogsNow(contentCatalogs)
    }

    void forcedSyncContentCatalogsNow(ContentCatalogEnum... contentCatalogs) {
        context.forcedSyncContentCatalogsNow(contentCatalogs)
    }

    void withImpexTemplateContexts(ImpexTemplateContext... impexTemplateContexts) {
        context.withImpexTemplateContexts(impexTemplateContexts)
    }

    void changeFieldType(ChangeFieldTypeContext... changeFieldTypeContexts) {
        context.changeFieldType(changeFieldTypeContexts)
    }

    void removeOrphanedTypes() { context.removeOrphanedTypes() }

    void schedulePartialUpdate(SolrEnum solrIndex, Set<String> indexedProperties) {
        context.schedulePartialUpdate(solrIndex, indexedProperties)
    }

    void fullReIndex(SolrEnum... solrIndexes) { context.fullReIndex(solrIndexes) }

    void removeSolrCore(SolrEnum... solrIndexes) { context.removeSolrCore(solrIndexes) }

    void withEnvironmentPatch(EnumSet<EnvironmentEnum> environments, Supplier<PatchContextDescriber> supplier) {
        context.withEnvironmentPatch(environments, supplier)
    }

    void createRelatedPatch() { context.createRelatedPatch() }

}

