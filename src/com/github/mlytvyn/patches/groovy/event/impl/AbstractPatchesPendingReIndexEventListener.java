package com.github.mlytvyn.patches.groovy.event.impl;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

abstract class AbstractPatchesPendingReIndexEventListener<T extends AbstractEvent> extends AbstractEventListener<T> {

    private static final Logger LOG = LogManager.getLogger(AbstractPatchesPendingReIndexEventListener.class);

    @Resource(name = "modelService")
    protected ModelService modelService;

    protected void replaceInstantTrigger(final CronJobModel cronJob) {
        final TriggerModel trigger = modelService.create(TriggerModel.class);
        trigger.setYear(0);
        trigger.setActivationTime(new Date());
        trigger.setCronJob(cronJob);

        final Collection<TriggerModel> oldTriggers = CollectionUtils.emptyIfNull(cronJob.getTriggers());
        modelService.removeAll(oldTriggers);

        cronJob.setTriggers(Collections.singletonList(trigger));

        LOG.info("SOLR cron job will be executed whenever the task engine is started. [cronJob: {}]", cronJob::getCode);
    }
}
