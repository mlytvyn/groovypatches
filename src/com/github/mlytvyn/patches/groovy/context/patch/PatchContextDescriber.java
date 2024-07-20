package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum;
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext;
import com.github.mlytvyn.patches.groovy.context.DropColumnContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.nio.file.Path;
import java.util.EnumSet;
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
     * This method will add a new nested patch to current patch, which will be executed AFTER applying current patch and BEFORE environment specific patch.
     * <p>
     * It is possible to register multiple nested Patches, they will be executed according to registration order.
     *
     * @param nested new nested patch
     * @return current patch
     */
    PatchContextDescriber withNestedPatch(PatchContextDescriber nested);

    /**
     * <p>This method will override default patch data folder.</p>
     * <p>This method calls {@link PatchContextDescriber#customPatchDataFolder(java.nio.file.Path, PatchDataFolderRelation)}
     * with the {@link PatchDataFolderRelation#RELEASE} dependency</p>
     *
     * <p>`patchdata/[release]/[patch]` with `patchdata/[release]/[customPath]`</p>
     *
     * @param customPatchDataFolder patch data folder
     * @return current patch
     */
    PatchContextDescriber customPatchDataFolder(Path customPatchDataFolder);

    /**
     * <p>This method will override default patch data folder.</p>
     * <p>By providing PatchDataFolderDependency parameter it will be possible to specify relation of the custom
     * patch folder to the ROOT (`patchdata`) folder or release folder of the Patch's (`patchdata/[release]`)
     * </p>
     * <p>
     * Samples of the overrides:
     * <p>ROOT dependant: `patchdata/[release]/[patch]` with `patchdata/[customPath]`</p>
     * <p>RELEASE dependant: `patchdata/[release]/[patch]` with `patchdata/[release]/[customPath]`</p>
     *
     * @param customPatchDataFolder patch data folder
     * @param relation              patch data folder relation
     * @return current patch
     */
    PatchContextDescriber customPatchDataFolder(Path customPatchDataFolder, PatchDataFolderRelation relation);

    /**
     * By using this method it will be possible to override default Impex Import Configuration set via properties for individual Patch.
     * Take a note, that Impex Import Configuration can be Impex specific, see {@link PatchContextDescriber#withImpexes(ImpexContext...)}
     *
     * @param config Impex Import Configuration
     * @return current patch
     */
    PatchContextDescriber impexImportConfig(ImpexImportConfig config);

    /**
     * This method will register ALL impexes of defined impexes for importing.
     * Default {@link ImpexContext} will be created for each passed impex.
     * <p>
     * If no args provided - all impexes will be imported according to default sort by file name
     * <br>
     * If some args provided - those specific impexes in exact order will be imported
     * <br>
     * If method IS NOT used in the patch - impexes will not be imported at all
     * <p>
     *
     * @param impexes blank | array of impexes
     * @return current patch
     */
    PatchContextDescriber withImpexes(String... impexes);

    /**
     * This method will register ALL impexes of defined impexes for importing by their path related to the extension_name/resources/extension_name/import.
     * Default {@link ImpexContext} will be created for each passed impex.
     * <p>
     * If no args provided - all impexes will be imported according to default sort by file name
     * <br>
     * If some args provided - those specific impexes in exact order will be imported
     * <br>
     * If method IS NOT used in the patch - impexes will not be imported at all
     * <p>
     *
     * @param impexes blank | array of impexes
     * @return current patch
     */
    PatchContextDescriber withFqnImpexes(String... impexes);

    /**
     * This method will register ALL impexes of defined impexes for importing.
     * This method gives more flexibility for import configuration for individual Impex
     * <p>
     * If no args provided - all impexes will be imported according to default sort by file name
     * <br>
     * If some args provided - those specific impexes in exact order will be imported
     * <br>
     * If method IS NOT used in the patch - impexes will not be imported at all
     *
     * @param impexContexts blank | array of Impex Contexts
     * @return current patch
     */
    PatchContextDescriber withImpexes(ImpexContext... impexContexts);

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
    PatchContextDescriber importEmailComponentTemplates(EnumSet<SiteEnum> sites, EmailComponentTemplateEnum... emailComponentTemplates);

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
     * @param impexTemplateContexts impex contexts
     * @return current patch
     */
    PatchContextDescriber withImpexTemplateContexts(ImpexTemplateContext... impexTemplateContexts);

    /**
     * This method will register specified field type changes which will be executed AFTER current Patch
     *
     * @param changeFieldTypeContexts change field type contexts
     * @return current patch
     */
    PatchContextDescriber changeFieldType(ChangeFieldTypeContext... changeFieldTypeContexts);

    /**
     * This method will register request for column removal, which will be executed AFTER current Patch.
     * <p>
     * Provide class of the Type and actual DB column name for each {@link DropColumnContext}
     *
     * @param dropColumnContexts drop column contexts
     * @return current patch
     */
    PatchContextDescriber dropColumn(DropColumnContext... dropColumnContexts);

    /**
     * This method request orphaned types removal BEFORE all patches
     *
     * @return current patch
     */
    PatchContextDescriber removeOrphanedTypes();

    /**
     * This method will register partial reindex for specific Solr Core and indexed properties of the Indexed Type
     *
     * @param indexedType       indexed type
     * @param indexedProperties solr indexed properties
     * @return current patch
     */
    PatchContextDescriber partialReIndex(SolrIndexedTypeEnum indexedType, String... indexedProperties);

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
     * This method will prepare and register environment specific patch.
     * It is possible to register multiple environment specific patches, they will be executed according to registration order.
     * <p>
     * It will be executed AFTER current patch and AFTER nested patch, which can be registered via {@link PatchContextDescriber#withNestedPatch(PatchContextDescriber)}
     * <p>
     * patch will be created only if environment matches
     *
     * @param environments target environments
     * @param supplier     patch supplier
     * @return current patch
     */
    PatchContextDescriber withEnvironmentPatch(EnumSet<EnvironmentEnum> environments, Supplier<PatchContextDescriber> supplier);

    /**
     * This is helper method which will created related Patch to be used as nested or environment patch.
     * <p>
     * By default, newly created patch will have same Release, ID & Number
     *
     * @return current patch
     */
    PatchContextDescriber createRelatedPatch();

    /**
     * This method will register all listed principals for resetting UserRights by removing all ACL entries from the aclentries table.
     * <p>
     * Only existing principals will be processed, any non-existing principal will be logged and excluded from the processing.
     * <p>
     * This action will be executed as part of the GlobalContext before any patches.
     * <p>
     * See {@link de.hybris.platform.persistence.security.ACLEntryJDBC}.
     *
     * @param principalUIDs array of principals UIDs
     * @return current patch
     */
    PatchContextDescriber resetUserRightsForPrincipals(String... principalUIDs);

}
