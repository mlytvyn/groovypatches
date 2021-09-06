

package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.ContentCatalogEnum;
import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum;
import com.github.mlytvyn.patches.groovy.EmailTemplateEnum;
import com.github.mlytvyn.patches.groovy.SiteEnum;
import com.github.mlytvyn.patches.groovy.SolrEnum;
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
}
