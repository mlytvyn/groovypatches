package com.github.mlytvyn.patches.groovy.context.global;

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1302845848288028643L;
    private final EnvironmentEnum currentEnvironment;
    private ImpexImportConfig impexImportConfig;
    private boolean removeOrphanedTypes;
    private final Set<SolrEnum> solrCoresForReIndex = new LinkedHashSet<>();
    private final Set<SolrEnum> solrCoresForRemoval = new LinkedHashSet<>();
    private final Set<EmailTemplateEnum> importEmailTemplates = new LinkedHashSet<>();
    private final Map<SolrIndexedTypeEnum, Set<String>> solrIndexedTypesForPartialReIndex = new HashMap<>();
    private final Map<EmailComponentTemplateEnum, EnumSet<SiteEnum>> importEmailComponentTemplates = new LinkedHashMap<>();
    // following values executed Before all patches and can be skipped
    private transient List<ReleaseContext> releases = Collections.emptyList();

    private GlobalContext(final EnvironmentEnum currentEnvironment) {
        this.currentEnvironment = currentEnvironment;
    }

    public static GlobalContext of(final EnvironmentEnum environment) {
        return new GlobalContext(environment);
    }

    public void scheduleSolrIndexedTypePartialReIndex(final SolrIndexedTypeEnum indexedType, final Collection<String> indexedProperties) {
        if (indexedProperties.isEmpty()) {
            return;
        }
        partiallyReIndexedSolrIndexedTypes()
                .computeIfAbsent(indexedType, type -> new HashSet<>())
                .addAll(indexedProperties);
    }

    public void scheduleSolrCoreFullReIndex(final List<SolrEnum> solrCores) {
        solrCoresForFullReIndex().addAll(solrCores);
    }

    public void scheduleSolrCoresForRemoval(final List<SolrEnum> solrCores) {
        solrCoresForRemoval().addAll(solrCores);
    }

    public ImpexImportConfig impexImportConfig() {
        return impexImportConfig;
    }

    public GlobalContext impexImportConfig(final ImpexImportConfig impexImportConfig) {
        this.impexImportConfig = impexImportConfig;
        return this;
    }

    public Map<SolrIndexedTypeEnum, Set<String>> partiallyReIndexedSolrIndexedTypes() {
        return solrIndexedTypesForPartialReIndex;
    }

    public Set<EmailTemplateEnum> importEmailTemplates() {
        return importEmailTemplates;
    }

    public Set<SolrEnum> solrCoresForRemoval() {
        return solrCoresForRemoval;
    }

    public Set<SolrEnum> solrCoresForFullReIndex() {
        return solrCoresForReIndex;
    }

    public boolean removeOrphanedTypes() {
        return removeOrphanedTypes;
    }

    public GlobalContext removeOrphanedTypes(final boolean removeOrphanedTypes) {
        this.removeOrphanedTypes = removeOrphanedTypes;
        return this;
    }

    public List<ReleaseContext> releases() {
        return releases == null
                ? Collections.emptyList()
                : releases;
    }

    public GlobalContext releases(final List<ReleaseContext> releases) {
        this.releases = releases;
        return this;
    }

    public EnvironmentEnum currentEnvironment() {
        return currentEnvironment;
    }

    public Map<EmailComponentTemplateEnum, EnumSet<SiteEnum>> importEmailComponentTemplates() {
        return importEmailComponentTemplates;
    }

    @Override
    public String toString() {
        return "GlobalContext{" +
                "currentEnvironment=" + currentEnvironment +
                ", releases=" + releases +
                '}';
    }
}
