package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext;
import com.github.mlytvyn.patches.groovy.context.ImpexContext;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface PatchContextDescriptor {

    /**
     * Unique automatically generated patch identifier. Usually a combination of the:
     * <p><strong>EXTENSIONNAME</strong> + "-" + <strong>releaseId</strong> + "-" + <strong>number</strong></p>
     * <br/>
     * Can be manually overridden via {@link PatchContextDescriber#hash(String)}
     *
     * @return unique hash of the patch
     */
    String hash();

    /**
     * This method will return full patch name:
     * <p>{@link PatchContextDescriptor#getReleaseContext()} + " | " + {@link PatchContextDescriptor#getNumber()} + " | " + {@link PatchContextDescriptor#getId()}</p>
     *
     * @return full patch name
     */
    String getName();

    /**
     * Acts as a decorator for {@link GlobalContext#getCurrentEnvironment()} and return current environment, which was identified via {@link EnvironmentInfoService#getEnvironment()}
     *
     * @return current {@link EnvironmentEnum}
     */
    EnvironmentEnum getCurrentEnvironment();

    /**
     * This method will return all requests to change a field type for current patch.
     *
     * @return unmodifiable list of all requests or empty list
     */
    List<ChangeFieldTypeContext> getChangeFieldTypeContexts();

    /**
     * This method will return patch # for current Patch.
     * <br/>
     * Usually should be represented as a number length of 4 (aka: 0005)
     *
     * @return patch #
     */
    String getNumber();

    /**
     * This method will return list of registered impex contexts for current patch.
     * <br/>
     * If list is not empty, each impex context will be used during impex import
     *
     * @return unmodifiable list of impex contexts
     */
    List<ImpexContext> getImpexContexts();

    /**
     * This method will return list of requested impex to import.
     * <br/>
     * If list is empty ALL impexes in the patchfolder will be imported according to natural sort order by name
     * If list is NULL impex import will be skipped
     *
     * @return null | unmodifiable list of impexes
     */
    List<String> getImpexes();

    /**
     * This method will return current patch ID
     * <br/>
     * Usually should be represented as a JIRA ticket ID: SAPHYBRIS-XXX, GP-YYY, SAPECOM-ZZZ
     *
     * @return current patch ID
     */
    String getId();

    /**
     * This method will return all content catalogs which were registered for synchronization withing the patch.
     * <br/>
     * It means that synchronization for those specific content catalogs will be executed right after the patch, not after the Release!
     * <p>
     * map.key - ContentCatalog id
     * map.value - is FORCED sync
     *
     * @return empty | unmodifiable map of content catalogs
     */
    Map<ContentCatalogEnum, Boolean> getContentCatalogsToBeSyncedNow();

    /**
     * This method will return set of environments for which this Patch is applicable.
     * <p>
     * By default Patch will be allowed for ALL environments
     * It is possible to override this value and make patch Environment specific via {@link PatchContextDescriber#environment(EnvironmentEnum...)}
     *
     * @return unmodifiable all envs | unmodifiable custom envs
     */
    Set<EnvironmentEnum> getEnvironments();

    /**
     * This method will return before action which will be executed before current Patch
     *
     * @return optional before action
     */
    Optional<Consumer<SystemSetupContext>> getBeforeConsumer();

    /**
     * This method will return after action which will be executed after current Patch
     *
     * @return optional after action
     */
    Optional<Consumer<SystemSetupContext>> getAfterConsumer();

    /**
     * This method will return human-friendly description of the current Patch.
     * <p>
     * Usually it should be set to JIRA ticket name
     *
     * @return patch description
     */
    String getDescription();

    /**
     * This method will return nested patch, if set.
     * <p>
     * There is not limitation in level of nesting, so you can have patch.nested(patch_1.nested(patch_2)
     *
     * @return optional nested patch
     */
    Optional<PatchContextDescriptor> getNestedPatch();

    /**
     * Each patch must be associated with specific release. This method will return current release context.
     *
     * @return release context
     */
    ReleaseContext getReleaseContext();

    /**
     * Each patch must be part of the global context. This method will return current global context.
     *
     * @return global context
     */
    GlobalContext getGlobalContext();

    /**
     * This is helper method which will ensure that patch methods can be executed in the target environment.
     * Mainly used by methods defined via {@link PatchContextDescriber}
     *
     * @return true / false
     */
    boolean isApplicable();

    /**
     * Negate of the {@link PatchContextDescriptor#isApplicable()}
     *
     * @return true / false
     */
    boolean isNotApplicable();

    /**
     * This method will return environment specific patch. Only one environment patch will be registered for current patch.
     *
     * @return optional environment patch
     */
    Optional<PatchContextDescriptor> getEnvironmentPatch();

    /**
     * This method will return target patch folder for current Patch.
     * <p>
     * Take a not that this patch folder always must be part of the current release returned by {@link PatchContextDescriptor#getReleaseContext()}
     * <p>
     * By default {@link PatchContextDescriptor#getId()} will be used. sample: SAPHYBRIS-1234
     * It can be overridden via {@link PatchContextDescriber#customPatchDataFolder(String)}
     *
     * @return patch folder for current patch
     */
    String getPatchDataFolder();
}
