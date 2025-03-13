package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import de.hybris.platform.core.initialization.SystemSetupContext;
import org.apache.poi.ss.formula.functions.T;

import java.util.Map;

public interface CatalogSynchronizer<T extends Enum<?>> {

    void synchronize(SystemSetupContext context, Map<T, Boolean> contentCatalogsToBeSynced, boolean parallelSync);
}
