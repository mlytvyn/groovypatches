

package com.github.mlytvyn.patches.groovy.context.release.actions.impl;

import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.util.ContentCatalogSynchronizer;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class ReleaseContextSyncContentCatalogsAction implements ReleaseContextAction {

    @Resource(name = "contentCatalogSynchronizer")
    private ContentCatalogSynchronizer contentCatalogSynchronizer;

    @Override
    public void execute(final SystemSetupContext context, final ReleaseContext release) {
        contentCatalogSynchronizer.synchronize(context, release.getContentCatalogsToBeSynced(), true);
    }

}
