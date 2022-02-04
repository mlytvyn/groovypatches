package com.github.mlytvyn.patches.groovy.context.release;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.core.model.ItemModel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true, fluent = true)
@Builder(builderMethodName = "internalBuilder")
@RequiredArgsConstructor
public class ReleaseContext implements Serializable {

    private static final long serialVersionUID = 1196114181425770979L;

    @NonNull
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String version;
    @NonNull
    @ToString.Include
    @EqualsAndHashCode.Include
    private final String id;
    private Map<ContentCatalogEnum, Boolean> contentCatalogsToBeSynced = new LinkedHashMap<>();
    private Set<ContentCatalogEnum> contentCatalogsToBeRemoved = new LinkedHashSet<>();
    private transient LinkedHashSet<PatchContextDescriptor> patches = new LinkedHashSet<>();

    public static ReleaseContextBuilder builder(final String version, final String id) {
        return internalBuilder().version(version).id(id);
    }

    public ReleaseContext removeContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            contentCatalogsToBeRemoved().addAll(Arrays.asList(contentCatalogs));
        }
        return this;
    }

    public ReleaseContext syncContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            Arrays.stream(contentCatalogs)
                    .forEach((final ContentCatalogEnum contentCatalog) -> contentCatalogsToBeSynced().putIfAbsent(contentCatalog, false));
        }
        return this;
    }

    public ReleaseContext forcedSyncContentCatalogs(final ContentCatalogEnum... contentCatalogs) {
        if (ArrayUtils.isNotEmpty(contentCatalogs)) {
            Arrays.stream(contentCatalogs)
                    .forEach((final ContentCatalogEnum contentCatalog) -> contentCatalogsToBeSynced().put(contentCatalog, true));
        }
        return this;
    }

}
