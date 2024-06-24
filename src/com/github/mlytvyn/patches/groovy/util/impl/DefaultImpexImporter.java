package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.commerceservices.setup.SetupImpexService;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.patch.PatchDataFolderRelation;
import com.github.mlytvyn.patches.groovy.util.ImpexImporter;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultImpexImporter implements ImpexImporter {

    @Resource(name = "extendedSetupImpexService")
    protected SetupImpexService setupImpexService;
    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public void importSingleImpex(final SystemSetupContext context, final String patchesFolder, final ImpexContext impex, final ImpexImportConfig impexImportConfig, final Map<String, Object> macroParameters) {
        logReporter.logInfo(context, String.format("Import: %s", impex.name()));

        final String impexPath = impex.isFqn()
                ? getFqnImpExPath(impex)
                : impex.name().contains(patchesFolder)
                ? getImpExName(impex)
                : getImpexPath(patchesFolder, impex);

        setupImpexService.importImpexFile(impexPath, impexImportConfig, macroParameters);
    }

    @Override
    public String getPatchDataFolder(final PatchContextDescriptor patch) {
        final String extensionName = configurationService.getConfiguration().getString("patches.groovy.project.extension.name");

        if (patch.getPatchDataFolderRelation() == PatchDataFolderRelation.ROOT) {
            return Paths.get(extensionName, "import", "patchdata", patch.getPatchDataFolder().toString()).toString();
        } else {
            return Paths.get(extensionName, "import", "patchdata", patch.getReleaseContext().id(), patch.getPatchDataFolder().toString()).toString();
        }
    }

    @Override
    public List<String> getImpexesForPatch(final String patchesFolder, final List<ImpexContext> impexes) {
        return impexes.stream()
                .map(impex -> impex.isFqn()
                        ? getFqnImpExPath(impex)
                        : getImpexPath(patchesFolder, impex))
                .collect(Collectors.toList());
    }

    protected String getFqnImpExPath(final ImpexContext impex) {
        return "/" + getFolderForFqnImpExFiles() + getImpExName(impex);
    }

    protected String getImpExName(final ImpexContext impex) {
        return impex.name().startsWith("/")
                ? impex.name()
                : "/" + impex.name();
    }

    protected String getFolderForFqnImpExFiles() {
        final String extensionName = configurationService.getConfiguration().getString("patches.groovy.project.extension.name");

        return Paths.get(extensionName, "import").toString();
    }

    protected String getImpexPath(final String patchesFolder, final ImpexContext impex) {
        return String.format("/%s%s%s", patchesFolder, File.separator, impex.name());
    }
}
