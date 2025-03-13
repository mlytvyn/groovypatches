package com.github.mlytvyn.patches.groovy.context.release.actions.impl;

import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.util.ProductCatalogSynchronizer;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class ReleaseContextSyncProductCatalogsAction implements ReleaseContextAction {

    @Resource(name = "productCatalogSynchronizer")
    private ProductCatalogSynchronizer productCatalogSynchronizer;

    @Override
    public void execute(final SystemSetupContext context, final ReleaseContext release) {
        productCatalogSynchronizer.synchronize(context, release.productCatalogsToBeSynced(), true);
    }

}
