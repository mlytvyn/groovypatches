

package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalPatchesException;
import com.github.mlytvyn.patches.groovy.util.EmailTemplateImporter;
import com.github.mlytvyn.patches.groovy.util.ImpexImporter;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;

public class DefaultEmailTemplateImporter  implements EmailTemplateImporter {

    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;
    @Resource(name = "groovyPatchesImpexImporter")
    protected ImpexImporter impexImporter;

    @Override
    public void importEmailTemplate(final SystemSetupContext context, final GlobalContext globalContext, final String template, final Map<String, Object> macroParameters) {
        final String patchesFolder = configurationService.getConfiguration().getString("patches.groovy.emails.folder");
        final String impex = String.format("%s%s%s", patchesFolder, File.separator, template);
        if (!impexImporter.importSingleImpex(context, impex, macroParameters)) {
            throw new GlobalPatchesException(globalContext, "Email template is not found [template: " + template + "]");
        }
    }
}
