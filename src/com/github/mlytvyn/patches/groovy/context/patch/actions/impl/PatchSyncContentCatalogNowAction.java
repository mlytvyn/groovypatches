package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.util.ContentCatalogSynchronizer;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class PatchSyncContentCatalogNowAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "contentCatalogSynchronizer")
    protected ContentCatalogSynchronizer contentCatalogSynchronizer;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        contentCatalogSynchronizer.synchronize(context, patch.getContentCatalogsToBeSyncedNow(), false);
    }

}
