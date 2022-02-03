package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;

public interface ConfigurationProvider {

    String getContentCatalogId(ContentCatalogEnum contentCatalog);

    String getEmailTemplate(EmailTemplateEnum emailTemplate);

    String getSiteCode(SiteEnum site);

    String getEmailComponentTemplate(EmailComponentTemplateEnum emailComponentTemplate);

    String getSolrCoreName(SolrEnum solr);
}
