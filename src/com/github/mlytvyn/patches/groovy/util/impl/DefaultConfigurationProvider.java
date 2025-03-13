package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
import com.github.mlytvyn.patches.groovy.SolrIndexedTypeEnum;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;

public class DefaultConfigurationProvider implements ConfigurationProvider {

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public String getContentCatalogId(final ContentCatalogEnum contentCatalog) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.catalog.content.%s.id", contentCatalog));
    }

    @Override
    public String getProductCatalogId(final ProductCatalogEnum productCatalog) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.catalog.product.%s.id", productCatalog));
    }

    @Override
    public String getEmailTemplate(final EmailTemplateEnum emailTemplate) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.emailTemplate.%s.template", emailTemplate));
    }

    @Override
    public String getSiteCode(final SiteEnum site) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.site.%s.uid", site));
    }

    @Override
    public String getEmailComponentTemplate(final EmailComponentTemplateEnum emailComponentTemplate) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.emailComponentTemplate.%s.template", emailComponentTemplate));
    }

    @Override
    public String getSolrCoreName(final SolrEnum solr) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.solr.index.%s.name", solr));
    }

    @Override
    public String getSolrIndexedTypeName(final SolrIndexedTypeEnum indexedType) {
        return configurationService.getConfiguration().getString(String.format("patches.groovy.solr.index.type.%s.identifier", indexedType));
    }

    @Override
    public String getSolrIndexedTypePartialCronJobPrefix(final SolrIndexedTypeEnum indexedType) {
        return configurationService.getConfiguration().getString("patches.groovy.solr.index.partial.cronJob.prefix", "patchesPartialReIndexCronJob_") + indexedType;
    }
}
