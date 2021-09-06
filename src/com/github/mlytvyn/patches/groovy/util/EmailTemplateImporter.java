

package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.Map;

public interface EmailTemplateImporter {

    void importEmailTemplate(SystemSetupContext context, GlobalContext globalContext, String template, Map<String, Object> macroParameters);
}
