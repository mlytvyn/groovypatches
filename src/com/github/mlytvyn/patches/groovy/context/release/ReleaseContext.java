package com.github.mlytvyn.patches.groovy.context.release;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ReleaseContext implements Serializable {

    private final String version;
    private final String id;
    private Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSynced;
    private Set<ContentCatalogEnum> contentCatalogsToBeRemoved;
    private transient LinkedHashSet<PatchContextDescriptor> patches;

    public ReleaseContext(final String version, final String id) {
        this.version = version;
        this.id = id;
    }

    public String getId() {
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

    public ReleaseContext removeContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            getContentCatalogsToBeRemoved().addAll(Arrays.asList(contentCatalogs));
        }
        return this;
    }

    public ReleaseContext syncContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            Arrays.stream(contentCatalogs)
                .forEach((final ContentCatalogEnum contentCatalog) -> getContentCatalogsToBeSynced().putIfAbsent(contentCatalog, false));
        }
        return this;
    }

    public ReleaseContext forcedSyncContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            Arrays.stream(contentCatalogs)
                .forEach((final ContentCatalogEnum contentCatalog) -> getContentCatalogsToBeSynced().put(contentCatalog, true));
        }
        return this;
    }

    public ReleaseContext syncAllContentCatalogs() {
        Arrays.stream(ContentCatalogEnum.values())
            .forEach((final ContentCatalogEnum contentCatalog) -> getContentCatalogsToBeSynced().putIfAbsent(contentCatalog, false));
        return this;
    }

    public Map<ContentCatalogEnum, Boolean> getContentCatalogsToBeSynced() {
        if (contentCatalogsToBeSynced == null) {
            contentCatalogsToBeSynced = new LinkedHashMap<>();
        }
        return contentCatalogsToBeSynced;
    }

    public Set<ContentCatalogEnum> getContentCatalogsToBeRemoved() {
        if (contentCatalogsToBeRemoved == null) {
            contentCatalogsToBeRemoved = new LinkedHashSet<>();
        }
        return contentCatalogsToBeRemoved;
    }

    public LinkedHashSet<PatchContextDescriptor> getPatches() {
        if (patches == null) {
            patches = new LinkedHashSet<>();
        }
        return patches;
    }

    public void setPatches(final LinkedHashSet<PatchContextDescriptor> patches) {
        this.patches = patches;
    }

    public String getVersion() {
        return version;
    }
}
