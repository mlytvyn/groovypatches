package com.github.mlytvyn.patches.groovy.event.impl;

import com.github.mlytvyn.patches.groovy.model.PatchesFullReIndexCronJobModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.event.events.AfterInitializationEndEvent;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Locale;

public class PatchesPendingFullReIndexEventListener extends AbstractPatchesPendingReIndexEventListener<AfterInitializationEndEvent> {

    @Resource(name = "cronJobService")
    private CronJobService cronJobService;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;
    @Resource(name = "modelService")
    private ModelService modelService;
    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;
    @Resource(name = "commonI18NService")
    private CommonI18NService commonI18NService;

    @Override
    protected void onEvent(final AfterInitializationEndEvent event) {
        final String cronJobName = configurationService.getConfiguration().getString("patches.groovy.solr.index.full.cronJob.name", "patchesFullReIndexCronJob");
        final CronJobModel cronJob = getCronJobModel(cronJobName);

        replaceInstantTrigger(cronJob);

        modelService.save(cronJob);
    }

    private CronJobModel getCronJobModel(final String cronJobName) {
        try {
            return cronJobService.getCronJob(cronJobName);
        } catch (final UnknownIdentifierException e) {
            return createCronJob(cronJobName);
        }
    }

    private CronJobModel createCronJob(final String cronJobName) {
        final CronJobModel cronJob = modelService.create(PatchesFullReIndexCronJobModel.class);
        cronJob.setCode(cronJobName);
        cronJob.setJob(retrieveServiceLayerJob());
        cronJob.setSessionLanguage(getSessionLanguage());

        modelService.save(cronJob);
        return cronJob;
    }

    private LanguageModel getSessionLanguage() {
        return commonI18NService.getLanguage(configurationService.getConfiguration().getString("patches.groovy.solr.index.full.cronJob.language", Locale.ENGLISH.getLanguage()));
    }

    private ServicelayerJobModel retrieveServiceLayerJob() {
        final String serviceLayerJobCode = configurationService.getConfiguration().getString("patches.groovy.solr.index.full.serviceLayerJob.name", "patchesFullReIndexIndexerJob");

        final FlexibleSearchQuery fxsQuery = new FlexibleSearchQuery("SELECT {pk} FROM {ServicelayerJob} WHERE {code} = ?code");
        fxsQuery.addQueryParameter("code", serviceLayerJobCode);

        final List<ServicelayerJobModel> serviceLayerJobs = flexibleSearchService.<ServicelayerJobModel>search(fxsQuery).getResult();
        if (serviceLayerJobs.isEmpty()) {
            final ServicelayerJobModel servicelayerJob = modelService.create(ServicelayerJobModel.class);
            servicelayerJob.setCode(serviceLayerJobCode);
            servicelayerJob.setSpringId("patchesFullReIndexJobPerformable");

            modelService.save(servicelayerJob);
            return servicelayerJob;
        }
        return serviceLayerJobs.get(0);
    }

}

