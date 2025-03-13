package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.Map;

public interface ProductCatalogSynchronizer extends CatalogSynchronizer<ProductCatalogEnum> {

    void synchronize(SystemSetupContext context, Map<ProductCatalogEnum, Boolean> contentCatalogsToBeSynced, boolean parallelSync);
}
