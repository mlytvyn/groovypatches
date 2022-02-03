package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.ImpexContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.util.ImpexImporter;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PatchImpexImportAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "groovyPatchesImpexImporter")
    private ImpexImporter impexImporter;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        if (patch.getImpexes() != null) {
            logReporter.logInfo(context, "Impex import started");
            importImpexes(context, patch);
            logReporter.logInfo(context, "Impex import completed");
        }
    }

    protected void importImpexes(final SystemSetupContext context, final PatchContextDescriptor patch) {
        final String patchesFolder = impexImporter.getPatchDataFolder(patch);
        if (patch.getImpexContexts().isEmpty()) {
            if (CollectionUtils.isEmpty(patch.getImpexes())) {
                importAllImpexes(context, patch, patchesFolder, Collections.emptyMap());
            } else {
                importSpecifiedImpexes(context, patchesFolder, patch.getImpexes(), Collections.emptyMap());
            }
        } else {
            for (final ImpexContext impexContext : patch.getImpexContexts()) {
                logReporter.logInfo(context, "Importing with impex context: " + impexContext.getName());
                if (CollectionUtils.isEmpty(patch.getImpexes())) {
                    importAllImpexes(context, patch, patchesFolder, impexContext.getMacroParameters());
                } else {
                    importSpecifiedImpexes(context, patchesFolder, patch.getImpexes(), impexContext.getMacroParameters());
                }
            }
        }
    }

    private void importSpecifiedImpexes(final SystemSetupContext context, final String patchesFolder, final List<String> impexes, final Map<String, Object> macroParameters) {
        impexImporter.getImpexesForPatch(patchesFolder, impexes)
            // no need to check is specific impex exists as it should be already checked by PatchSelfValidateAction
            .forEach(impexFile -> impexImporter.importSingleImpex(context, impexFile, macroParameters));
    }

    private void importAllImpexes(final SystemSetupContext context, final PatchContextDescriptor patch, final String patchesFolder, final Map<String, Object> macroParameters) {
        try {
            Stream.of(new PathMatchingResourcePatternResolver(this.getClass().getClassLoader()).getResources(patchesFolder + "/**/*.impex"))
                .map(resource -> {
                    try {
                        return resource.getFile().getPath();
                    } catch (final IOException e) {
                        throw new PatchException(patch, e.getMessage(), e);
                    }
                })
                .map(path -> path.substring(path.indexOf(patchesFolder)))
                .forEachOrdered(impex -> impexImporter.importSingleImpex(context, impex, macroParameters));
        } catch (final IOException e) {
            throw new PatchException(patch, e.getMessage(), e);
        }
    }
}
