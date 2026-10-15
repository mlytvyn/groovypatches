package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.*
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext
import com.github.mlytvyn.patches.groovy.context.DropColumnContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig
import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.dsl.PatchDsl
import de.hybris.platform.core.initialization.SystemSetupContext
import de.hybris.platform.core.model.ItemModel

import java.util.function.Consumer
import java.util.function.Supplier

class PatchSpec {

    protected final PatchContextDescriber context

    PatchSpec(PatchContextDescriber context) {
        this.context = context
    }

    protected PatchSpec createSpec(PatchContextDescriber relatedContext) { new PatchSpec(relatedContext) }

    def methodMissing(String name, args) {
        if (context.metaClass.respondsTo(context, name, args as Object[])) {
            return context.invokeMethod(name, args)
        }

        throw new MissingMethodException(name, getClass(), args as Object[])
    }

    void hash(String hash) { context.hash(hash) }

    void environment(EnvironmentEnum... environments) { context.environment(environments) }

    void description(String description) { context.description(description) }

    void impexes(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexesSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new ImpexesSpec(context)
        closure()
    }

    void before(Consumer<SystemSetupContext> consumer) { context.before(consumer) }

    void after(Consumer<SystemSetupContext> consumer) { context.after(consumer) }

    void withNestedPatch(PatchContextDescriber nested) { context.withNestedPatch(nested) }

    void nestedPatch(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchSpec) Closure closure
    ) {
        def related = context.createRelatedPatch()
        PatchDsl.registerPatch(createSpec(related), closure)
        context.withNestedPatch(related)
    }

    void customPatchDataFolder(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchDataFolderSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new PatchDataFolderSpec(context)
        closure()
    }

    void impexImportConfig(ImpexImportConfig config) { context.impexImportConfig(config) }

    void withImpexes(String... impexes) { context.withImpexes(impexes) }

    void withFqnImpexes(String... impexes) { context.withFqnImpexes(impexes) }

    void withImpexes(ImpexContext... impexContexts) { context.withImpexes(impexContexts) }

    void importEmailTemplates(EmailTemplateEnum... emailTemplates) { context.importEmailTemplates(emailTemplates) }

    void emailComponentTemplates(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new EmailComponentTemplatesSpec(context)
        closure()
    }

    void catalogs(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = CatalogsSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new CatalogsSpec(context)
        closure()
    }

    void withImpexTemplateContexts(ImpexTemplateContext... impexTemplateContexts) {
        context.withImpexTemplateContexts(impexTemplateContexts)
    }

    void templateContext(
            String description,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ImpexTemplateContextSpec) Closure closure = {}
    ) {
        context.withImpexTemplateContexts(ImpexTemplateContextSpec.build(description, closure))
    }

    void changeFieldType(ChangeFieldTypeContext... changeFieldTypeContexts) {
        context.changeFieldType(changeFieldTypeContexts)
    }

    void changeFieldType(
            Class<? extends ItemModel> targetClass,
            String fieldName,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ChangeFieldTypeSpec) Closure closure
    ) {
        context.changeFieldType(ChangeFieldTypeSpec.build(targetClass, fieldName, closure))
    }

    void dropColumn(DropColumnContext... dropColumnContexts) { context.dropColumn(dropColumnContexts) }

    void dropColumn(Class<? extends ItemModel> targetClass, String columnName) {
        context.dropColumn(DropColumnContext.of(targetClass, columnName))
    }

    void removeOrphanedTypes() { context.removeOrphanedTypes() }

    void resetUserRightsForPrincipals(String... principalUIDs) { context.resetUserRightsForPrincipals(principalUIDs) }

    void solr(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = SolrSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new SolrSpec(context)
        closure()
    }

    void withEnvironmentPatch(EnumSet<EnvironmentEnum> environments, Supplier<PatchContextDescriber> supplier) {
        context.withEnvironmentPatch(environments, supplier)
    }

    void environmentPatch(
            EnvironmentEnum environment,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchSpec) Closure closure
    ) {
        environmentPatch(EnumSet.of(environment), closure)
    }

    void environmentPatch(
            Collection<EnvironmentEnum> environments,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = PatchSpec) Closure closure
    ) {
        def targetEnvironments = EnumSet.copyOf(environments)

        context.withEnvironmentPatch(targetEnvironments, { ->
            def related = context.createRelatedPatch()
            related.environment(targetEnvironments as EnvironmentEnum[])
            PatchDsl.registerPatch(createSpec(related), closure)
            related
        })
    }

    void createRelatedPatch() { context.createRelatedPatch() }

}

