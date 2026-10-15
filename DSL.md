# DSL-driven patch declaration

The same patch can be declared as a `patch { }` block instead of a chain of `patchContext` calls. The `patch` closure is bound to every Patch script next to the `patchContext`, so no import or base script declaration is needed:

```groovy
patch {
    description "HYB-1 | Sample patch"

    impexes {
        impex "import.impex"
    }
}
```

Alternatively, `PatchScript` can be declared as the base script of the patch file, which provides the same `patch` method:

```groovy
import com.github.mlytvyn.patches.groovy.dsl.PatchScript
import groovy.transform.BaseScript

@BaseScript PatchScript patchScript
```

Every `PatchContextDescriber` method is available inside the block without the `patch.` prefix, and each nested entry is declared as a nested block instead of a static factory chain:

| Nested block                                  | Replaces                                                         |
|-----------------------------------------------|------------------------------------------------------------------|
| `patch { }`                                   | `(patchContext as PatchContextDescriber)...`                     |
| `nestedPatch { }`                             | `withNestedPatch(patch.createRelatedPatch())`                    |
| `environmentPatch(env) { }`                   | `withEnvironmentPatch(EnumSet.of(env), { -> ... })`              |
| `environmentPatch([env1, env2]) { }`          | `withEnvironmentPatch(EnumSet.of(env1, env2), { -> ... })`       |
| `impexes { impex("x.impex") { } }`            | `withImpexes(ImpexContext.of("x.impex"))`                        |
| `impexes { impex "x.impex" }`                 | `withImpexes("x.impex")`                                         |
| `impexes { fqnImpex "x.impex" }`              | `withFqnImpexes("x.impex")`                                      |
| `impexes { all() }`                           | `withImpexes()`                                                  |
| `impexes { config { } }`                      | `impexImportConfig(ImpexImportConfig.create()...)`               |
| `templateContext("...") { }`                  | `withImpexTemplateContexts(ImpexTemplateContext.of("...")...)`   |
| `catalogs { content { sync ... } }`            | `syncContentCatalogs(...)`                                       |
| `catalogs { product { forcedSyncNow ... } }`   | `forcedSyncProductCatalogsNow(...)`                              |
| `emails { templates ... } `                   | `importEmailTemplates(...)`                                      |
| `emails { componentTemplates { allSites { } } }` | `importEmailComponentTemplates(...)`                          |
| `emails { componentTemplates { forSites(...) { } } }` | `importEmailComponentTemplates(EnumSet.of(...), ...)`     |
| `solr { fullReIndex ... }`                    | `fullReIndex(...)`                                               |
| `solr { partialReIndex { } }`                 | `partialReIndex(type, properties...)`                            |
| `solr { removeCores ... }`                    | `removeSolrCore(...)`                                            |
| `changeFieldType(Type, "field") { }`          | `changeFieldType(ChangeFieldTypeContext.of(Type, "field")...)`   |
| `dropColumn Type, "column"`                   | `dropColumn(DropColumnContext.of(Type, "column"))`               |
| `customPatchDataFolder { release "folder" }`  | `customPatchDataFolder(Paths.get("folder"))`                     |
| `customPatchDataFolder { root "folder" }`     | `customPatchDataFolder(Paths.get("folder"), ...Relation.ROOT)`   |

Notes:

* Every block resolves with `Closure.DELEGATE_FIRST`, so Script context variables, like `configurationProvider`, remain accessible within any block
* An environment specific patch is restricted to its environments before the block is evaluated, so on a non-matching environment its operations are skipped instead of being applied to the Global Context
* `withNestedPatch`, `withImpexes`, `withFqnImpexes`, `withImpexTemplateContexts` and `withEnvironmentPatch` were dropped from `PatchSpec` - every one of them is fully superseded by a nested block (`nestedPatch { }`, `impexes { }`, `templateContext(...) { }`, `environmentPatch(...) { }`). The raw `changeFieldType(ChangeFieldTypeContext...)` and `dropColumn(DropColumnContext...)` passthroughs stay, next to their block equivalents, since they accept pre-built context objects that a block cannot represent any more concisely
* `customPatchDataFolder`, the catalog operations, `emails` and `solr` are the exceptions, they are available as a block only - `release` / `root` replace the `PatchDataFolderRelation` parameter, `catalogs { content { } / product { } }` replaces the ten `[forced]Sync[Content|Product]Catalogs[Now]` / `remove[Content|Product]Catalogs` methods, `emails { templates ... / componentTemplates { allSites { } / forSites([...]) { } } }` replaces `importEmailTemplates` and the two `importEmailComponentTemplates` overloads - `forSites` takes a `Collection<SiteEnum>` (list literal) followed by a `template(...)` block, and `solr { fullReIndex / partialReIndex { } / removeCores }` replaces the flat `fullReIndex`, `partialReIndex` and `removeSolrCore` methods, with `partialReIndex` itself being block-only (`type` / `properties`)
* Any method not declared on `PatchSpec` (including the removed `with...` ones) is still forwarded to the underlying `patchContext` if it exists there, see [Project specific operations](#project-specific-operations)

## Project specific operations

Operations of a custom `PatchContextDescriber`, registered via own `PatchContextFactory`, are available in the `patch { }` block without any DSL customization - any method unknown to `PatchSpec` is forwarded to the underlying context:

```groovy
patch {
    description "HYB-1 | Sample patch"
    customOperation "<param>"
}
```

Only a custom nested block requires own Spec. Extend `PatchSpec` and override `createSpec`, so that the custom Spec is used for the `patch { }` block as well as for every `nestedPatch { }` / `environmentPatch(...) { }` of that patch:

```groovy
class MyPatchSpec extends PatchSpec {

    MyPatchSpec(PatchContextDescriber context) {
        super(context)
    }

    @Override
    protected PatchSpec createSpec(PatchContextDescriber relatedContext) { new MyPatchSpec(relatedContext) }

    void customBlock(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = MyCustomSpec) Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new MyCustomSpec(context)
        closure()
    }
}
```

Register it by overriding the `patchDslRegistrar` bean:

```groovy
class MyPatchDslRegistrar extends DefaultPatchDslRegistrar {

    @Override
    protected PatchSpec createSpec(PatchContextDescriber context) { new MyPatchSpec(context) }
}
```

```xml
<alias name="myPatchDslRegistrar" alias="patchDslRegistrar"/>
<bean id="myPatchDslRegistrar" class="com.myproject.patches.groovy.dsl.MyPatchDslRegistrar"/>
```

`PatchScript` exposes the same `createSpec` hook for the `@BaseScript` based declaration.

## Sample Groovy patch, DSL based

Same patch as [Sample Groovy patch](README.md#sample-groovy-patch), declared via the DSL.

```groovy
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum
import com.github.mlytvyn.patches.groovy.EnvironmentEnum
import com.github.mlytvyn.patches.groovy.ProductCatalogEnum
import com.github.mlytvyn.patches.groovy.SiteEnum
import com.github.mlytvyn.patches.groovy.SolrEnum
import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.util.Config

patch {
// Specify hash in case of long-live shared branches, with unknown release date, otherwise hash will be generated based on extensionName + releaseId + patchNumber + patchId
    hash "<your custom internal Identifier of the patch>"
// It is possible to adjust patch data folder, it will be still in the same release, but if you'd like to place all impexes from different patches to the same folder it will be possible
// `release` keeps it release related (`patchdata/[release]`), `root` makes it root patch folder related (`patchdata`)
    customPatchDataFolder {
        release "<custom patch folder within the release folder>"
    }
    customPatchDataFolder {
        root "<custom patch folder within the patchdata folder>"
    }
// Limits patch to specific environments, by default - applicable to all
    environment EnvironmentEnum.LOCAL
// Specify optional description, which will be stored in the `SystemSetupAudit`
    description "<Optional description of the patch>"
// Specify principal UIDs for which User Rights should be removed from the aclentries table
    resetUserRightsForPrincipals "sampleEmployee@email.com", "employeegroup", "backofficerole"

// Allows simple modification of the field type in the DB, it will execute SQL query, customize `PatchChangeFieldTypeAction` to adjust SQL or introduce new DB
// see OOTB `core-advanced-deployment.xml` for allowed DB specific column types, use DB value, not Hybris one
    changeFieldType(ProductModel, ProductModel.NAME) {
        dbFieldType Config.DatabaseName.HANA, "TEXT"
        dbFieldType Config.DatabaseName.MYSQL, "TEXT"
        dbFieldType Config.DatabaseName.SQLSERVER, "NCLOB"
    }
// Allows simple removal of the DB column for existing Type by execution of the SQL query, customize `PatchDropColumnAction` in case of a need
// Important: various edge cases may cause this operation to fail, for example, if column is being used in the DB Index.
    dropColumn ProductModel, "p_legacy_column"

    /*
      It is possible to import various ImpEx files according to the following import rules:
        1. It is allowed to specify ImpEx files to import via multiple declaration of the `impex(...)` method
        2. All ImpEx files within the Path Folder will be imported only in case of `all()` method usage
        3. If `impex` method used several times, only those ImpEx files will be imported
     */
    impexes {
// If specified will override default Impex Import Configuration set via properties for current Patch only, can be overridden for individual Impex
        config {
            failOnError true
        }
// Imports all impexes of the patch data folder in natural order
        all()
// Imports specific impexes according to defined order
        impex "import_1.impex"
        impex "import_2.impex"
// It is also possible to adjust individual impex import
        impex("import_3.impex") {
            legacyMode true
            enableCodeExecution true
            failOnError true
        }
// Impexes can be referenced by their path related to the `<extension>/resources/<extension>/import` as well
        fqnImpex "common/import_4.impex"
// Besides that it is possible to specify multiple Template Contexts per ImpEx
        impex("import_5.impex") {
            templateContext("first execution") {
                macroParameter "siteUid", configurationProvider.getSiteCode(SiteEnum.DUMMY)
            }
            templateContext("second execution") {
                macroParameter "siteUid", configurationProvider.getSiteCode(SiteEnum.NOT_DUMMY)
            }
        }
    }

// It is possible to specify custom Impex Template Contexts, it will lead to import of all impexes of the patch with each defined Template Context
// enables possibility to create "template" based impexes and pass different params as a Map
// in the Impex file each parameter will be injected as `$parameterName`
// take a note that ImpEx with own Template Contexts will be executed also for each Template Context set per Patch
    templateContext("Site Dummy") {
        macroParameter "siteUid", configurationProvider.getSiteCode(SiteEnum.DUMMY)
    }
    templateContext("Site Not Dummy") {
        macroParameter "siteUid", configurationProvider.getSiteCode(SiteEnum.NOT_DUMMY)
    }

// Executes custom groovy logic before anything else, `setup` argument points to `SystemSetupContext`
    before { setup -> }
// Executes custom groovy logic after everything else, `setup` argument points to `SystemSetupContext`
    after { setup -> }

    catalogs {
        content {
// Registers Content Catalog synchronization with `force=false` attribute, will be executed after current release
            sync ContentCatalogEnum.DUMMY
// Registers Content Catalog synchronization with `force=false` attribute, will be executed after current patch
            syncNow ContentCatalogEnum.DUMMY
// Registers Content Catalog synchronization with `force=true` attribute, will be executed after the release
            forcedSync ContentCatalogEnum.DUMMY
// Registers Content Catalog synchronization with `force=true` attribute, will be executed after current patch
            forcedSyncNow ContentCatalogEnum.DUMMY
// Registers Content Catalog removal, will be executed after current release
            remove ContentCatalogEnum.DUMMY
        }
        product {
// Registers Product Catalog synchronization with `force=false` attribute, will be executed after current release
            sync ProductCatalogEnum.DUMMY
// Registers Product Catalog synchronization with `force=false` attribute, will be executed after current patch
            syncNow ProductCatalogEnum.DUMMY
// Registers Product Catalog synchronization with `force=true` attribute, will be executed after the release
            forcedSync ProductCatalogEnum.DUMMY
// Registers Product Catalog synchronization with `force=true` attribute, will be executed after current patch
            forcedSyncNow ProductCatalogEnum.DUMMY
// Registers Product Catalog removal, will be executed after current release
            remove ProductCatalogEnum.DUMMY
        }
    }

// Registering this operation will remove all orphaned types
    removeOrphanedTypes()
    emails {
// Specify email templates which should be re-imported
        templates EmailTemplateEnum.DUMMY
        componentTemplates {
// Specify email component templates which should be re-imported for all Sites
            allSites() {
                template EmailComponentTemplateEnum.BANNER_COMPONENT
            }
// Specify email component templates which should be re-imported for a single Site
            forSites([SiteEnum.DUMMY]) {
                template EmailComponentTemplateEnum.CMS_IMAGE_COMPONENT
            }
// Specify email component templates which should be re-imported for defined Sites, one template per line
            forSites([SiteEnum.DUMMY, SiteEnum.NOT_DUMMY]) {
                template EmailComponentTemplateEnum.CMS_LINK_COMPONENT
                template EmailComponentTemplateEnum.CMS_PARAGRAPH_COMPONENT
            }
        }
    }
    solr {
// Remove indexed SOLR core
        removeCores SolrEnum.DUMMY
// Schedule delayed full reindex of the SOLR core, executed after the patching process, on next server start up
        fullReIndex SolrEnum.DUMMY
// Schedule delayed partial reindex of the SOLR core, executed after the patching process, on next server start up
        partialReIndex {
            type SolrIndexedTypeEnum.DUMMY
            properties "name", "id", "etc"
        }
    }

// Allows creation of the nested patches, executed after the current patch and before environment specific patches
    nestedPatch {
        description "<Nested patch description>"
        impexes {
            impex "import_nested.impex"
        }
    }

// Allows creation of the environment specific patches, accepts a single environment or a list of them
    environmentPatch(EnvironmentEnum.LOCAL) {
        impexes {
            impex "import_local.impex"
        }
    }
}
```
