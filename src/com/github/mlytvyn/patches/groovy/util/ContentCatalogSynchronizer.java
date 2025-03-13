package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.Map;

public interface ContentCatalogSynchronizer extends CatalogSynchronizer<ContentCatalogEnum> {

    void synchronize(SystemSetupContext context, Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSynced, boolean parallelSync);
}
