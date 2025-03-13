package com.github.mlytvyn.patches.groovy.util;

import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum;

public interface ConfigurationProvider {

    String getContentCatalogId(ContentCatalogEnum contentCatalog);

    String getProductCatalogId(ProductCatalogEnum productCatalog);

    String getEmailTemplate(EmailTemplateEnum emailTemplate);

    String getSiteCode(SiteEnum site);

    String getEmailComponentTemplate(EmailComponentTemplateEnum emailComponentTemplate);

    String getSolrCoreName(SolrEnum solr);

    String getSolrIndexedTypeName(SolrIndexedTypeEnum indexedType);

    String getSolrIndexedTypePartialCronJobPrefix(SolrIndexedTypeEnum indexedType);
}
