package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.patch.PatchValidationException;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.util.ImpexImporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

public class PatchValidateAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "groovyPatchesImpexImporter")
    private ImpexImporter impexImporter;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        if (patch.isApplicable()) {
            validatePatchImpexes(patch);
        }
    }

    protected void validatePatchImpexes(final PatchContextDescriptor patch) {
        if (patch.getImpexes() == null) {
            // patch without impexes
            return;
        }

        final String patchDataFolder = impexImporter.getPatchDataFolder(patch);
        if (patch.getImpexes().isEmpty()) {
            final org.springframework.core.io.Resource resource = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader()).getResource(patchDataFolder);

            if (!resource.exists()) {
                throw new PatchValidationException(patch, "Mandatory patch data folder is missing, cannot proceed. [patch data folder: " + patchDataFolder + "]");
            }
        } else {
            impexImporter.getImpexesForPatch(patchDataFolder, patch.getImpexes()).forEach(impexFile -> {
                try (final InputStream resourceAsStream = getClass().getResourceAsStream(impexFile)) {
                    if (resourceAsStream == null) {
                        throw new PatchValidationException(patch, "Mandatory impex file not found, cannot proceed. [file: " + impexFile + "]");
                    }
                } catch (final IOException e) {
                    throw new PatchValidationException(patch, "Mandatory impex file not found, cannot proceed. [file: " + impexFile + "]");
                }
            });
        }
    }
}
