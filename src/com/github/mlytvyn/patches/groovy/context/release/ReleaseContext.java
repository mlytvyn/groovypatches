package com.github.mlytvyn.patches.groovy.context.release;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

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

    public void forcedSyncContentCatalogs(final List<ContentCatalogEnum> contentCatalogs) {
        contentCatalogs.forEach((final ContentCatalogEnum contentCatalog) -> contentCatalogsToBeSynced().put(contentCatalog, true));
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
