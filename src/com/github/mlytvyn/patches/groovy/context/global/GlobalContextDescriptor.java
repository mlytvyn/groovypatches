package com.github.mlytvyn.patches.groovy.context.global;

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GlobalContextDescriptor {

    Set<SolrEnum> getIndexesToBeReindexed();

    Set<SolrEnum> getCoresToBeRemoved();

    Map<SolrEnum, Set<String>> getPartialUpdateProperties();

    EnvironmentEnum getCurrentEnvironment();

    boolean isRemoveOrphanedTypes();

    Map<EmailComponentTemplateEnum, EnumSet<SiteEnum>> getImportEmailComponentTemplates();

    Set<EmailTemplateEnum> getImportEmailTemplates();

    List<ReleaseContext> getReleases();
}
