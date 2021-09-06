

package com.github.mlytvyn.patches.groovy.context.global;

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalContext implements Serializable, GlobalContextDescriber, GlobalContextDescriptor {

    private static final long serialVersionUID = 1302845848288028643L;
    private final EnvironmentEnum currentEnvironment;
    private Set<SolrEnum> indexesToBeReindexed;
    private Set<SolrEnum> coresToBeRemoved;
    private Set<EmailTemplateEnum> importEmailTemplates;
    private Map<SolrEnum, Set<String>> partialUpdateProperties;
    private Map<EmailComponentTemplateEnum, EnumSet<SiteEnum>> importEmailComponentTemplates;
    // following values executed Before all patches and can be skipped
    private transient List<ReleaseContext> releases;
    private transient boolean removeOrphanedTypes;

    public GlobalContext(final EnvironmentEnum currentEnvironment) {
        this.currentEnvironment = currentEnvironment;
    }

    @Override
    public String toString() {
        return "GlobalContext{" +
            "currentEnvironment=" + currentEnvironment +
            ", releases=" + getReleases() +
            '}';
    }

    @Override
    public GlobalContext removeOrphanedTypes() {
        this.removeOrphanedTypes = true;
        return this;
    }

    @Override
    public GlobalContext importEmailTemplates(final EmailTemplateEnum... emailTemplates) {
        if (ArrayUtils.isNotEmpty(emailTemplates)) {
            getImportEmailTemplates().addAll(Arrays.asList(emailTemplates));
        }
        return this;
    }

    @Override
    public GlobalContext importEmailComponentTemplates(final EnumSet<SiteEnum> sites, final EnumSet<EmailComponentTemplateEnum> emailComponentTemplates) {
        emailComponentTemplates.forEach(emailComponentTemplate -> {
            if (getImportEmailComponentTemplates().containsKey(emailComponentTemplate)) {
                getImportEmailComponentTemplates().get(emailComponentTemplate).addAll(sites);
            } else {
                getImportEmailComponentTemplates().put(emailComponentTemplate, sites);
            }
        });
        return this;
    }

    @Override
    public GlobalContext importEmailComponentTemplates(final EmailComponentTemplateEnum... emailComponentTemplates) {
        if (ArrayUtils.isNotEmpty(emailComponentTemplates)) {
            Arrays.stream(emailComponentTemplates)
                .forEach(emailComponentTemplate -> getImportEmailComponentTemplates().put(emailComponentTemplate, EnumSet.allOf(SiteEnum.class)));
        }
        return this;
    }

    @Override
    public GlobalContext schedulePartialUpdate(final SolrEnum solrIndex, final Set<String> indexedProperties) {
        if (indexedProperties.isEmpty()) {
            return this;
        }
        getPartialUpdateProperties().computeIfAbsent(solrIndex, index -> new HashSet<>())
            .addAll(indexedProperties);
        return this;
    }

    @Override
    public GlobalContext fullReIndex(final SolrEnum solrIndex) {
        getIndexesToBeReindexed().addAll(List.of(solrIndex));
        return this;
    }

    @Override
    public GlobalContext fullReIndex(final List<SolrEnum> solrIndexes) {
        getIndexesToBeReindexed().addAll(solrIndexes);
        return this;
    }

    @Override
    public GlobalContext removeSolrCores(final List<SolrEnum> solrIndexes) {
        getCoresToBeRemoved().addAll(solrIndexes);
        return this;
    }

    @Override
    public Set<SolrEnum> getIndexesToBeReindexed() {
        if (indexesToBeReindexed == null) {
            indexesToBeReindexed = new LinkedHashSet<>();
        }
        return indexesToBeReindexed;
    }

    @Override
    public Set<SolrEnum> getCoresToBeRemoved() {
        if (coresToBeRemoved == null) {
            coresToBeRemoved = new LinkedHashSet<>();
        }
        return coresToBeRemoved;
    }

    @Override
    public Map<SolrEnum, Set<String>> getPartialUpdateProperties() {
        if (partialUpdateProperties == null) {
            partialUpdateProperties = new HashMap<>();
        }
        return partialUpdateProperties;
    }

    @Override
    public EnvironmentEnum getCurrentEnvironment() {
        return currentEnvironment;
    }

    @Override
    public boolean isRemoveOrphanedTypes() {
        return removeOrphanedTypes;
    }

    @Override
    public Map<EmailComponentTemplateEnum, EnumSet<SiteEnum>> getImportEmailComponentTemplates() {
        if (importEmailComponentTemplates == null) {
            importEmailComponentTemplates = new LinkedHashMap<>();
        }
        return importEmailComponentTemplates;
    }

    @Override
    public Set<EmailTemplateEnum> getImportEmailTemplates() {
        if (importEmailTemplates == null) {
            importEmailTemplates = new LinkedHashSet<>();
        }
        return importEmailTemplates;
    }

    @Override
    public List<ReleaseContext> getReleases() {
        return releases == null ? Collections.emptyList() : releases;
    }

    public void setReleases(final List<ReleaseContext> releases) {
        this.releases = releases;
    }
}
