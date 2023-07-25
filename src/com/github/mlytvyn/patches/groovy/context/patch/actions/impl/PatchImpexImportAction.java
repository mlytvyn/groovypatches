package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportException;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexTemplateContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
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
import java.util.stream.Collectors;
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
                importSpecifiedImpexes(context, patch, patch.getImpexes(), patchesFolder, Collections.emptyMap());
            }
        } else {
            for (final ImpexTemplateContext impexTemplateContext : patch.getImpexContexts()) {
                logReporter.logInfo(context, "Importing with impex context: " + impexTemplateContext.description());
                if (CollectionUtils.isEmpty(patch.getImpexes())) {
                    importAllImpexes(context, patch, patchesFolder, impexTemplateContext.macroParameters());
                } else {
                    importSpecifiedImpexes(context, patch, patch.getImpexes(), patchesFolder, impexTemplateContext.macroParameters());
                }
            }
        }
    }

    private void importAllImpexes(final SystemSetupContext context, final PatchContextDescriptor patch, final String patchesFolder, final Map<String, Object> macroParameters) {
        try {
            final List<ImpexContext> impexes = Stream.of(new PathMatchingResourcePatternResolver(this.getClass().getClassLoader()).getResources(patchesFolder + "/**/*.impex"))
                    .map(resource -> {
                        try {
                            return resource.getFile().getPath();
                        } catch (final IOException e) {
                            throw new PatchException(patch, e.getMessage(), e);
                        }
                    })
                    .map(path -> path.substring(path.indexOf(patchesFolder)))
                    .map(ImpexContext::of)
                    .collect(Collectors.toList());
            importSpecifiedImpexes(context, patch, impexes, patchesFolder, macroParameters);
        } catch (final IOException | ImpexImportException e) {
            throw new PatchException(patch, e.getMessage(), e);
        }
    }

    /**
     * No need to check is specific impex exists as it should be already checked by {@link PatchValidateAction}
     */
    private void importSpecifiedImpexes(final SystemSetupContext context, final PatchContextDescriptor patch, final List<ImpexContext> impexes, final String patchesFolder, final Map<String, Object> macroParameters) {
        impexes.forEach(impex -> {
            final ImpexImportConfig impexImportConfig = impex.config()
                    .orElseGet(() -> patch.getImpexImportConfig()
                            .orElseGet(() -> patch.getGlobalContext().impexImportConfig())
                    );

            impexImporter.importSingleImpex(context, patchesFolder, impex, impexImportConfig, macroParameters);
        });
    }

}
