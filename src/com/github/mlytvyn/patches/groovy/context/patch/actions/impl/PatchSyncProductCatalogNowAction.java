package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.util.ProductCatalogSynchronizer;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class PatchSyncProductCatalogNowAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "productCatalogSynchronizer")
    protected ProductCatalogSynchronizer productCatalogSynchronizer;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        productCatalogSynchronizer.synchronize(context, patch.getProductCatalogsToBeSyncedNow(), false);
    }

}
