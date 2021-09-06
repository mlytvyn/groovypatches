

package com.github.mlytvyn.patches.groovy.context.patch.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextFactory;
import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.PatchesCollector;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.scripting.engine.ScriptingLanguagesService;
import de.hybris.platform.core.initialization.SystemSetupAuditDAO;
import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptExecutionResult;
import de.hybris.platform.scripting.engine.exception.ScriptingException;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultPatchesCollector implements PatchesCollector<GlobalContext> {

    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;
    @Resource(name = "systemSetupAuditDAO")
    protected SystemSetupAuditDAO systemSetupAuditDAO;
    @Resource(name = "extendedScriptingLanguagesService")
    protected ScriptingLanguagesService scriptingLanguagesService;
    @Resource(name = "groovyPatchContextFactory")
    protected PatchContextFactory<GlobalContext, PatchContext<GlobalContext>> patchContextFactory;

    @Override
    public LinkedHashSet<PatchContextDescriptor> collect(final GlobalContext globalContext, final ReleaseContext release, final List<String> plainPatches) {
        return plainPatches.stream()
            .map(plainPatch -> plainPatch.split("_", 2))
            .map(patchNumber2patchId -> createPatchContext(globalContext, release, patchNumber2patchId))
            // filter out Patches which are already applied
            // we have to filter out by hash two times, this one will be valid for Patches 2.0, implemented directly in Groovy
            // while legacy Patches will be filtered after script evaluation, as they may have manually set hash via hash() method
            .filter(Predicate.not(patch -> systemSetupAuditDAO.isPatchApplied(patch.hash())))
            // ensure that Patch script is compilable, init with exact patch config
            .peek(patch -> precompilePatchScript(release, patch))
            // filter out legacy Patches which were applied before Patches 2.0
            .filter(Predicate.not(patch -> systemSetupAuditDAO.isPatchApplied(patch.hash())))
            // filter out Patches per applicable environment
            // this will filter out only MAIN Patch, we still can have main patch for all envs and few env specific patches assigned or even nested one (which also may be env specific)
            .filter(patchContext -> patchContext.getEnvironments().contains(globalContext.getCurrentEnvironment()))
            // TODO: any additional checks can be added here
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected PatchContext<GlobalContext> createPatchContext(final GlobalContext globalContext, final ReleaseContext release, final String[] patchNumber2patchId) {
        final String patchNumber = patchNumber2patchId[0];
        final String patchId = patchNumber2patchId[1];
        return patchContextFactory.createContext(globalContext, release, patchNumber, patchId);
    }

    protected void precompilePatchScript(final ReleaseContext release, final PatchContext<GlobalContext> patchContext) {
        final var extensionName = configurationService.getConfiguration().getString("patches.groovy.project.extension.name");
        final String scriptPath = "classpath://" + extensionName + "/releases/" + release.getVersion() + "/" + release.getId() + "/" + patchContext.getNumber() + "_" + patchContext.getId() + ".groovy";

        if (configurationService.getConfiguration().getBoolean("patches.groovy.invalidateCachedScripts", true)) {
            scriptingLanguagesService.invalidateCachedScript(scriptPath);
        }

        final ScriptExecutable scriptExecutable = scriptingLanguagesService.getExecutableByURI(scriptPath);

        try {
            final ScriptExecutionResult scriptExecutionResult = scriptExecutable.execute(Map.of(
                "patchContext", patchContext
            ));
            if (!scriptExecutionResult.isSuccessful()) {
                throw new PatchException(patchContext, "Script evaluation failed with: " + scriptExecutionResult.getErrorWriter().toString());
            }
        } catch (final ScriptingException e) {
            throw new PatchException(patchContext, "Script compilation failed.", e);
        }
    }

}
