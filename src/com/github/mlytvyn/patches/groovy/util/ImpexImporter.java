

package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.List;
import java.util.Map;

public interface ImpexImporter {
    boolean importSingleImpex(SystemSetupContext context, String impexFile, Map<String, Object> macroParameters);

    String getPatchDataFolder(PatchContextDescriptor patch);

    List<String> getImpexesForPatch(String patchesFolder, List<String> impexes);
}
