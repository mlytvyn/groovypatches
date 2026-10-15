package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class ContentCatalogsSpec {

    private final PatchContextDescriber context

    ContentCatalogsSpec(PatchContextDescriber context) {
        this.context = context
    }

    void sync(ContentCatalogEnum... contentCatalogs) { context.syncContentCatalogs(contentCatalogs) }

    void syncNow(ContentCatalogEnum... contentCatalogs) { context.syncContentCatalogsNow(contentCatalogs) }

    void forcedSync(ContentCatalogEnum... contentCatalogs) { context.forcedSyncContentCatalogs(contentCatalogs) }

    void forcedSyncNow(ContentCatalogEnum... contentCatalogs) { context.forcedSyncContentCatalogsNow(contentCatalogs) }

    void remove(ContentCatalogEnum... contentCatalogs) { context.removeContentCatalogs(contentCatalogs) }
}
