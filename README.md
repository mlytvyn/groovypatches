Groovy-based patching framework for SAP Commerce Cloud (Hybris)
=====================

# TODO's

* Introduce default `CurrentEnvironmentProvider` for default CCv2 setup
* Introduce Product Catalog synchronization action
* Add `Reset BackOffice` action
* Improve readme
* Create hybris specific branches, implement version specific `ExtendedSetupSyncJobService`

# How to use

* Create new blank extension from `ygroovypatches` template extension - https://github.com/mlytvyn/ygroovypatches
* Create/customize `SystemSetup` class in the newly created custom extension. It is possible to have different set of
  patches for `Init` and `Update` (just in case of existing solution)

```java

@Service
@SystemSetup(extension = CustompatchesConstants.EXTENSIONNAME)
public class CustomPatchesSystemSetup extends GroovyPatchesSystemSetup {

    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.INIT)
    public void executeAllPatches(final SystemSetupContext context) {
        executePatches(context, "/releases/**/**/*.groovy");
    }

    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.UPDATE)
    public void executeUpdatePatches(final SystemSetupContext context) {
        executePatches(context, "/releases/2.0/**/*.groovy");
    }
}
```

* Introduce project specific implementation for `CurrentEnvironmentProvider` as it may differ from Solution to Solution
* Folders reference

| Data           | Path                                                                                                 |
|----------------|------------------------------------------------------------------------------------------------------|
| Groovy patches | `<custom>patches/resources/<custom>patches/releases/<optional group>/<release>/<JIRA-TICKET>.groovy` |
| Patch Impexes  | `<custom>patches/resources/<custom>patches/import/patchdata/<release>/<patch id>`                    |
| Email impexes| `<custom>patches/resources/<custom>patches/import/patchdata/<emails>`                                |

* Adjust `<custom>patches-beans.xml` according to your project setup

```xml
<?xml version="1.0" encoding="ISO-8859-1"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="beans.xsd">

    <enum class="com.github.mlytvyn.patches.groovy.EnvironmentEnum">
        <value>NA</value>
        <value>LOCAL</value>
    </enum>

    <enum class="com.github.mlytvyn.patches.groovy.SolrEnum">
        <value>TEST_SOLR_ID</value>
    </enum>

    <enum class="com.github.mlytvyn.patches.groovy.SiteEnum">
        <value>TEST_SITE_ID</value>
    </enum>

    <enum class="com.github.mlytvyn.patches.groovy.EmailTemplateEnum">
        <value>TEST_EMAIL_TEMPLATE</value>
    </enum>

    <enum class="com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum">
        <value>TEST_EMAIL_COMPONENT_TEMPLATE</value>
    </enum>

    <enum class="com.github.mlytvyn.patches.groovy.ProductCatalogEnum">
        <value>TEST_PRODUCT_CATALOG</value>
    </enum>

    <enum class="com.github.mlytvyn.patches.groovy.ContentCatalogEnum">
        <value>TEST_CONTENT_CATALOG</value>
    </enum>
</beans>
```

* Adjust `project.properties`

```properties
patches.groovy.project.extension.name=custompatches
log4j2.threadContext.PatchesId.enabled=true
log4j2.threadContext.PatchId.enabled=true
patches.groovy.emails.folder=custompatches/import/emails
# --------
# Solr index configuration
# --------
patches.groovy.solr.index.TEST_SOLR_ID.name=${test.solr.facet.search.config.name}
# --------
# Email component templates configuration
# --------
patches.groovy.emailComponentTemplate.TEST_EMAIL_COMPONENT_TEMPLATE.template=email-testComponent.impex
# --------
# Email templates configuration
# --------
patches.groovy.emailTemplate.TEST_EMAIL_TEMPLATE.template=email-testEmail.impex
# --------
# Content Catalogs configuration
# --------
patches.groovy.catalog.content.TEST_CONTENT_CATALOG.id=${test.content.catalog}
# --------
# Sites configuration
# --------
patches.groovy.site.TEST_SITE_ID.uid=${test.site.uid}
```

* Create new groovy-based Patch files and optional related impexes.
  * Each patch name must follow naming pattern: `<int number>`.`<patch name>`.groovy, sample `0001_HYB-1.groovy`
* Provided with `ygroovypatches` `patch_contributor.gdsl` should provide Script context specific variable `patchContext` in the Intellij IDEA

# Sample Groovy patch

```groovy
//import com.custompatches.patches.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum
import com.github.mlytvyn.patches.groovy.EnvironmentEnum
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig
import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.SiteEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchDataFolderRelation
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.util.Config

import java.nio.file.Paths

def cp = (configurationProvider as ConfigurationProvider)
// if `patchContext` is not properly registered in the IDE, use the following cast
def patch = (patchContext as PatchContextDescriber)
patch
// By extending PatchContextDescriptor and customizing Patch creation via PatchFactory it is possible to add own operations to default PatchContext, which will be executed according to defined order
//.customOperation(...)
// Or it is possible to define patch specific custom operations, which will be executed right away 
// Specify hash in case of long-live shared branches, with unknown release date, otherwise hash will be generated based on release id + patch id  
        .hash("<your custom internal Identifier of the patch")
// It is possible to adjust patch data folder, it will be still in the same release, but if you'd like to place all impexes from different patches to the same folder it will be possible
        .customPatchDataFolder(Paths.get("<custom patch folder within the release folder>"))
// It is also possible to adjust patch data folder in a way it will be root patch folder related (`patchdata`)
        .customPatchDataFolder(Paths.get("<custom patch folder within the release folder>"), PatchDataFolderRelation.ROOT)
// Limits patch to specific environments, by default - applicable to all 
        .environment(EnvironmentEnum.LOCAL)
// Allows creation of the environment specific patch, corresponding Related Patch have to be created in that case to create new context
// Supplied environment specific patch will be lazily evaluated only in the environment it was registered for
        .withEnvironmentPatch(EnumSet.allOf(EnvironmentEnum), { -> patch.createRelatedPatch() })
// Allows creation of the nested patches, which can be created via `patch.createRelatedPatch()`
        .withNestedPatch(patch.createRelatedPatch())
// Allows simple modification of the field type in the DB, it will execute SQL query, customize `PatchChangeFieldTypeAction` to adjust SQL or introduce new DB
// see OOTB `core-advanced-deployment.xml` for allowed DB specific column types, use DB value, not Hybris one
        .changeFieldType(
                ChangeFieldTypeContext.of(ProductModel.class, ProductModel.NAME)
                        .dbFieldType(Config.DatabaseName.HANA, "TEXT")
                        .dbFieldType(Config.DatabaseName.MYSQL, "TEXT")
                        .dbFieldType(Config.DatabaseName.SQLSERVER, "NCLOB")
        )
// Specify optional description, which will be
        .description("<Optional description of the patch>")
// If specified will override default Impex Import Configuration set via properties for current Patch only, can be overridden for individual Impex via ImpexContext
        .impexImportConfig(
                ImpexImportConfig.create().failOnError(true)
        )
// If specified without any parameters, all impexes will be imported in natural order, otherwise only specified impexes will be imported according to defined order  
        .withImpexes()
        .withImpexes(
                "import_1.impex",
                "import_2.impex"
        )
// It is also possible to adjust individual impex import via ImpexContext
        .withImpexes(
                ImpexContext.of("import_1.impex").legacyMode(true).enableCodeExecution(true).failOnError(true),
                ImpexContext.of("import_2.impex")
        )
// It is possible to specify custom Impex Template Contexts, it will lead to import of all impexes specified via `.withImpexes` with each defined ImpexTemplateContext
// enables possibility to create "template" based impexes and pass different params as a Map
// similar approach is used for Impexes imported via Addon, see AddOnConfigDataImportService
// in the Impex file each parameter will be injected as `$parameterName`
        .withImpexTemplateContexts(
                ImpexTemplateContext.of("Site Dummy")
                        .macroParameter("siteUid", cp.getSiteCode(SiteEnum.DUMMY)),
                ImpexTemplateContext.of("Site Not Dummy")
                        .macroParameter("siteUid", cp.getSiteCode(SiteEnum.NOT_DUMMY))
        )
// Executes custom groovy logic before anything else, `setup` argument points to `SystemSetupContext`
        .before({ setup -> })
// Executes custom groovy logic after everything else, `setup` argument points to `SystemSetupContext`
        .after({ setup -> })
// Registers Content Catalog synchronization with `force=true` attribute, will be executed after the release
        .forcedSyncContentCatalogs(ContentCatalogEnum.DUMMY)
// Registers Content Catalog synchronization with `force=true` attribute, will be executed after current patch
        .forcedSyncContentCatalogsNow(ContentCatalogEnum.DUMMY)
// Registers Content Catalog synchronization with `force=false` attribute, will be executed after current release
        .syncContentCatalogs()
// Registers Content Catalog synchronization with `force=false` attribute, will be executed after current patch
        .syncContentCatalogsNow()
// Registering this operation will remove all orphaned types
        .removeOrphanedTypes()
// Specify email templates which should be re-imported
        .importEmailTemplates(EmailTemplateEnum.DUMMY)
// Specify email component templates which should be re-imported for all Sites
        .importEmailComponentTemplates(EmailComponentTemplateEnum.BANNER_COMPONENT)
// Specify email component templates which should be re-imported for defined sites
        .importEmailComponentTemplates(EnumSet.allOf(SiteEnum.class),
                EmailComponentTemplateEnum.BANNER_COMPONENT,
                EmailComponentTemplateEnum.CMS_IMAGE_COMPONENT,
                EmailComponentTemplateEnum.CMS_LINK_COMPONENT,
                EmailComponentTemplateEnum.CMS_PARAGRAPH_COMPONENT
        )
```
