package com.github.mlytvyn.patches.groovy.commerceservices.setup;

import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;

import java.util.Map;

public interface SetupImpexService extends de.hybris.platform.commerceservices.setup.SetupImpexService {

    void importImpexFile(String impexPath, ImpexImportConfig impexImportConfig, Map<String, Object> macroParameters);
}
