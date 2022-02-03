package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.util.ImpexImporter;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultImpexImporter implements ImpexImporter {

    @Resource(name = "setupImpexService")
    protected SetupImpexService setupImpexService;
    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public boolean importSingleImpex(final SystemSetupContext context, final String impexFile, final Map<String, Object> macroParameters) {
        logReporter.logInfo(context, String.format("Import: %s", impexFile));
        final String file = impexFile.startsWith("/") ? impexFile : String.format("/%s", impexFile);
        return setupImpexService.importImpexFile(file, macroParameters, true);
    }

    @Override
    public String getPatchDataFolder(final PatchContextDescriptor patch) {
        final var extensionName = configurationService.getConfiguration().getString("patches.groovy.project.extension.name");
        return Paths.get(extensionName, "import", "patchdata", patch.getReleaseContext().getId(), patch.getPatchDataFolder()).toString();
    }

    @Override
    public List<String> getImpexesForPatch(final String patchesFolder, final List<String> impexes) {
        return impexes.stream()
            .map(impex -> String.format("/%s%s%s", patchesFolder, File.separator, impex)).collect(Collectors.toList());
    }

}
