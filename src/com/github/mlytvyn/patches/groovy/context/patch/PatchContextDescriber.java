

package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext;
import com.github.mlytvyn.patches.groovy.context.ImpexContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface PatchContextDescriber {

    /**
     * Must be used only for LEGACY patches, not groovy based.
     *
     * @param hash existing hash of the patch
     * @return current patch
     */
    PatchContextDescriber hash(String hash);

    /**
     * This method will set environment on which this patch can be applied
     * <p>
     * be default it is set to ALL envs
     *
     * @param environments new environments
     * @return current patch
     */
    PatchContextDescriber environment(EnvironmentEnum... environments);

    /**
     * This method will set human-friendly description of the Patch, usually a JIRA ticket name
     *
     * @param description patch description
     * @return current patch
     */
    PatchContextDescriber description(String description);

    /**
     * This method will register custom logic executor which will be invoked before applying Patch
     *
     * @param consumer before consumer
     * @return current patch
     */
    PatchContextDescriber before(Consumer<SystemSetupContext> consumer);

    /**
     * This method will register custom logic executor which will be invoked after applying Patch
     *
     * @param consumer after consumer
     * @return current patch
     */
    PatchContextDescriber after(Consumer<SystemSetupContext> consumer);

    /**
     * This method will add new nested patch to current patch, which will be executed AFTER applying current patch and BEFORE environment specific patch
     *
     * @param nested new nested patch
     * @return current patch
     */
    PatchContextDescriber nested(PatchContextDescriber nested);

    /**
     * This method will override default patch data. It have to be always part of the current patch release
     *
     * @param customPatchDataFolder patch data folder
     * @return current patch
     */
    PatchContextDescriber customPatchDataFolder(String customPatchDataFolder);

    /**
     * This method will register ALL impexes of defined impexes for importing.
     * <p>
     * If no args provided - all impexes will be imported according to default sort by file name
     * If some args provided - those specific impexes in exact order will be imported
     * If method IS NOT used in the patch - impexes will not be imported at all
     *
     * @param impexes blank | array of impexes
     * @return current patch
     */
    PatchContextDescriber withImpexes(String... impexes);

    /**
     * This method will register following email templates for import, they will be added to {@link GlobalContext}
     * <p>
     * if no args provided, nothing will be added.
     *
     * @param emailTemplates email templates
     * @return current patch
     */
    PatchContextDescriber importEmailTemplates(EmailTemplateEnum... emailTemplates);

    /**
     * This method will register following email component templates for import, they will be added to {@link GlobalContext}
     * <p>
     * if no args provided, nothing will be added.
     *
     * @param sites                   sites
     * @param emailComponentTemplates templates
     * @return current patch
     */
    PatchContextDescriber importEmailComponentTemplates(EnumSet<SiteEnum> sites, EnumSet<EmailComponentTemplateEnum> emailComponentTemplates);

    /**
     * This method will register following email component templates for import, they will be added to {@link GlobalContext}
     * <p>
     * if no args provided, nothing will be added.
     *
     * @param emailComponentTemplates templates
     * @return current patch
     */
    PatchContextDescriber importEmailComponentTemplates(EmailComponentTemplateEnum... emailComponentTemplates);

    /**
     * Do not use it as it does not respect multicountry
     *
     * @return current patch
     */
    @Deprecated(forRemoval = true, since = "Must not be used as it does not respect multicountry catalog setup")
    PatchContextDescriber syncAllContentCatalogs();

    /**
     * This method will register specified content catalogs to be synced AFTER current Patch release
     * <p>
     * By default such catalog sync will not be FORCED
     *
     * @param contentCatalogs content catalogs
     * @return current patch
     */
    PatchContextDescriber syncContentCatalogs(ContentCatalogEnum... contentCatalogs);

    /**
     * This method will register specified content catalogs to be FORCED synced AFTER current Patch release
     * <p>
     * If there is already a content catalog registered for sync, it will be overridden due FORCE
     *
     * @param contentCatalogs contetn catalogs
     * @return current patch
     */
    PatchContextDescriber forcedSyncContentCatalogs(ContentCatalogEnum... contentCatalogs);

    /**
     * This method will register content catalogs for removal AFTER current Patch release
     *
     * @param contentCatalogs content catalogs
     * @return current patch
     */
    PatchContextDescriber removeContentCatalogs(ContentCatalogEnum... contentCatalogs);

    /**
     * This method will register specified content catalogs to be synced AFTER current Patch
     *
     * @param contentCatalogs content catalogs
     * @return current patch
     */
    PatchContextDescriber syncContentCatalogsNow(ContentCatalogEnum... contentCatalogs);

    /**
     * This method will register specified content catalogs to be FORCED synced AFTER current Patch
     *
     * @param contentCatalogs content catalogs
     * @return current patch
     */
    PatchContextDescriber forcedSyncContentCatalogsNow(ContentCatalogEnum... contentCatalogs);

    /**
     * This method will register additional impex contexts for current Patch which should be used during impex import
     *
     * @param impexContexts impex contexts
     * @return current patch
     */
    PatchContextDescriber withImpexContexts(ImpexContext... impexContexts);

    /**
     * Same as {@link PatchContextDescriber#withImpexContexts(ImpexContext...)}
     *
     * @param impexContexts impex contenxts
     * @return current patch
     */
    PatchContextDescriber withImpexContexts(List<ImpexContext> impexContexts);

    /**
     * This method will register specified field type changes which will be executed AFTER current Patch
     *
     * @param changeFieldTypeContexts change field type contexts
     * @return current patch
     */
    PatchContextDescriber changeFieldType(ChangeFieldTypeContext... changeFieldTypeContexts);

    /**
     * This method request orphaned types removal BEFORE all patches
     *
     * @return current patch
     */
    PatchContextDescriber removeOrphanedTypes();

    /**
     * This method will register reindex for specific Solr Core and specific indexed properties
     *
     * @param solrIndex         solr core
     * @param indexedProperties solr indexed properties
     * @return current patch
     */
    PatchContextDescriber schedulePartialUpdate(SolrEnum solrIndex, Set<String> indexedProperties);

    /**
     * This method will register request for full reindex of the Solr Core
     *
     * @param solrIndexes solr cores
     * @return current patch
     */
    PatchContextDescriber fullReIndex(SolrEnum... solrIndexes);

    /**
     * This method will register request for removal of the Solr Core
     *
     * @param solrIndexes solr cores
     * @return current patch
     */
    PatchContextDescriber removeSolrCore(SolrEnum... solrIndexes);

    /**
     * This method will prepare and register environment specific patch. Only 1 environment patch can exist for current patch.
     * <p>
     * It will be executed AFTER current patch and AFTER nested patch, which can be registered via {@link PatchContextDescriber#nested(PatchContextDescriber)}
     * <p>
     * patch will be created only if environment matches
     *
     * @param environments target environments
     * @param supplier     patch supplier
     * @return current patch
     */
    PatchContextDescriber environmentPatch(EnumSet<EnvironmentEnum> environments, Supplier<PatchContextDescriber> supplier);

    /**
     * This is helper method which will created related Patch to be used as nested or environment patch.
     * <p>
     * By default newly created patch will have same Release, ID & Number
     *
     * @return current patch
     */
    PatchContextDescriber createRelatedPatch();

}
