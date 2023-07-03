package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.context.impex.ImpexContext;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.List;
import java.util.Map;

public interface ImpexImporter {

    String getPatchDataFolder(PatchContextDescriptor patch);

    List<String> getImpexesForPatch(String patchesFolder, List<ImpexContext> impexes);

    void importSingleImpex(SystemSetupContext context, ImpexContext impex, ImpexImportConfig impexImportConfig, Map<String, Object> macroParameters);
}
