package com.github.mlytvyn.patches.groovy.context.release;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
  List of Content Catalogs for synchronization can be updated in a way that: FORCED sync will always override previous value
 */
public class ReleaseContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1196114181425770979L;

    private final String version;
    private final String id;
    private final Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSynced = new LinkedHashMap<>();
    private final Set<ContentCatalogEnum> contentCatalogsToBeRemoved = new LinkedHashSet<>();
    private final Map<ProductCatalogEnum, Boolean> productCatalogsToBeSynced = new LinkedHashMap<>();
    private final Set<ProductCatalogEnum> productCatalogsToBeRemoved = new LinkedHashSet<>();
    private transient Set<PatchContextDescriptor> patches = new LinkedHashSet<>();

    private ReleaseContext(final String version, final String id) {
        this.version = version;
        this.id = id;
    }

    public static ReleaseContext of(final String version, final String id) {
        return new ReleaseContext(version, id);
    }

    public void syncContentCatalogs(final List<ContentCatalogEnum> contentCatalogs) {
        contentCatalogs.forEach((final ContentCatalogEnum contentCatalog) -> contentCatalogsToBeSynced().putIfAbsent(contentCatalog, false));
    }

    public void syncProductCatalogs(final List<ProductCatalogEnum> contentCatalogs) {
        contentCatalogs.forEach((final ProductCatalogEnum productCatalog) -> productCatalogsToBeSynced().putIfAbsent(productCatalog, false));
    }

    public void forcedSyncContentCatalogs(final List<ContentCatalogEnum> contentCatalogs) {
        contentCatalogs.forEach((final ContentCatalogEnum contentCatalog) -> contentCatalogsToBeSynced().put(contentCatalog, true));
    }

    public void forcedSyncProductCatalogs(final List<ProductCatalogEnum> productCatalogs) {
        productCatalogs.forEach((final ProductCatalogEnum productCatalog) -> productCatalogsToBeSynced().put(productCatalog, true));
    }

    public Set<PatchContextDescriptor> patches() {
        return patches == null
                ? Collections.emptySet()
                : patches;
    }

    public ReleaseContext patches(final Set<PatchContextDescriptor> patches) {
        this.patches = patches;
        return this;
    }

    public Set<ContentCatalogEnum> contentCatalogsToBeRemoved() {
        return contentCatalogsToBeRemoved;
    }

    public Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSynced() {
        return contentCatalogsToBeSynced;
    }

    public Set<ProductCatalogEnum> productCatalogsToBeRemoved() {
        return productCatalogsToBeRemoved;
    }

    public Map<ProductCatalogEnum, Boolean> productCatalogsToBeSynced() {
        return productCatalogsToBeSynced;
    }

    public String version() {
        return version;
    }

    public String id() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ReleaseContext that = (ReleaseContext) o;
        return version.equals(that.version) && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, id);
    }

    @Override
    public String toString() {
        return version + ":" + id;
    }
}
