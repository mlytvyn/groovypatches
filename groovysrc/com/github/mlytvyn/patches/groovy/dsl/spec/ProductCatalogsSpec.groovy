package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.ProductCatalogEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class ProductCatalogsSpec {

    private final PatchContextDescriber context

    ProductCatalogsSpec(PatchContextDescriber context) {
        this.context = context
    }

    void sync(ProductCatalogEnum... productCatalogs) { context.syncProductCatalogs(productCatalogs) }

    void syncNow(ProductCatalogEnum... productCatalogs) { context.syncProductCatalogsNow(productCatalogs) }

    void forcedSync(ProductCatalogEnum... productCatalogs) { context.forcedSyncProductCatalogs(productCatalogs) }

    void forcedSyncNow(ProductCatalogEnum... productCatalogs) { context.forcedSyncProductCatalogsNow(productCatalogs) }

    void remove(ProductCatalogEnum... productCatalogs) { context.removeProductCatalogs(productCatalogs) }
}
