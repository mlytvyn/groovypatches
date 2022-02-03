package com.github.mlytvyn.patches.groovy.context.global;

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface GlobalContextDescriber {

    GlobalContextDescriber removeOrphanedTypes();

    GlobalContextDescriber importEmailTemplates(EmailTemplateEnum... emailTemplates);

    GlobalContextDescriber importEmailComponentTemplates(EnumSet<SiteEnum> sites, EnumSet<EmailComponentTemplateEnum> emailComponentTemplates);

    GlobalContextDescriber importEmailComponentTemplates(EmailComponentTemplateEnum... emailComponentTemplates);

    GlobalContextDescriber schedulePartialUpdate(SolrEnum solrIndex, Set<String> indexedProperties);

    GlobalContextDescriber fullReIndex(SolrEnum solrIndex);

    GlobalContextDescriber fullReIndex(List<SolrEnum> solrIndexes);

    GlobalContextDescriber removeSolrCores(List<SolrEnum> solrIndexes);

}
