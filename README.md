Groovy-based patching framework for SAP Commerce Cloud (Hybris)
=====================

# TODO's

* Create template extension for project specific implementation
* Introduce default `CurrentEnvironmentProvider` for default CCv2 setup
* Introduce extendable Factory for `ReleaseContext`
* Introduce Product Catalog syncronization action
* Add `Reset BackOffice` action
* Add possibility to fail patch if Impex import failed
* Create custom beans.xml template for Enums based on `global-enumtemplate.vm`, which will support declaration without
  any default values
* Improve readme

# How to use

* Create new blank extension
* Add new extension dependency on `groovypatches`
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
    public void executePatches(final SystemSetupContext context) {
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

# Sample Groovy patch

```groovy
//import com.custompatches.patches.context.patch.PatchContextDescriber
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum
import com.github.mlytvyn.patches.groovy.EnvironmentEnum
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.util.Config

patch = (patchContext as PatchContextDescriber)
patch
// By extending PatchContextDescriptor and customizing Patch creation via PatchFactory it is possible to add own operations to default PatchContext, which will be executed according to defined order
//.customOperation(...)
// Or it is possible to define patch specific custom operations, which will be executed right away 
//.with(true,  { it -> })
// Specify hash in case of long-live shared branches, with unknown release date, otherwise hash will be generated based on release id + patch id  
        .hash("<your custom internal Identifier of the patch")
// It is possible to adjust patch data folder, it will be still in the same release, but if you'd like to place all impexes from different patches to the same folder it will be possible
        .customPatchDataFolder("<custom patch folder within the release folder>")
// Limits patch to specific environments, by default - applicable to all 
        .environment()
// Allows creation of the environment specific patch, corresponding Related Patch have to be created in that case to create new context 
        .environmentPatch(EnumSet.allOf(EnvironmentEnum), { -> patch.createRelatedPatch() })
// Allows creation of the nested patches, corresponding Related Patch have to be created in that case to create new context
        .nested(patch.createRelatedPatch())
// Allows simple modification of the field type in the DB, it will execute SQL query, customize `PatchChangeFieldTypeAction` to adjust SQL or introduce new DB
// see OOTB `core-advanced-deployment.xml` for allowed DB specific column types, use DB value, not Hybris one
        .changeFieldType(
                ChangeFieldTypeContext.builder(ProductModel.class, ProductModel.NAME)
                        .dbFieldType(Config.DatabaseName.HANA, "TEXT")
                        .dbFieldType(Config.DatabaseName.MYSQL, "TEXT")
                        .dbFieldType(Config.DatabaseName.SQLSERVER, "NCLOB")
                        .build()
        )
// Specify optional description, which will be
        .description("<Optional description of the patch>")
// If specified without any parameters, all impexes will be imported in natural order, otherwise only specified impexes will be imported according to defined order  
        .withImpexes()
// It is possible to specify custom Impex Contexts 
        .withImpexContexts()
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
// Specify email component templates which should be re-imported
        .importEmailComponentTemplates(EmailComponentTemplateEnum.DUMMY)
```
