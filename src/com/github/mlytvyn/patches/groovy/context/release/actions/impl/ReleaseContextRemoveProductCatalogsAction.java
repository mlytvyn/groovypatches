package com.github.mlytvyn.patches.groovy.context.release.actions.impl;

import com.github.mlytvyn.patches.groovy.ProductCatalogEnum;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.util.ConfigurationProvider;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.RemoveCatalogVersionCronJobModel;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

public class ReleaseContextRemoveProductCatalogsAction implements ReleaseContextAction {

    @Resource(name = "cronJobService")
    protected CronJobService cronJobService;
    @Resource(name = "modelService")
    protected ModelService modelService;
    @Resource(name = "sessionService")
    protected SessionService sessionService;
    @Resource(name = "userService")
    protected UserService userService;
    @Resource(name = "catalogService")
    protected CatalogService catalogService;
    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "configurationProvider")
    private ConfigurationProvider configurationProvider;

    @Override
    public void execute(final SystemSetupContext context, final ReleaseContext release) {
        release.productCatalogsToBeRemoved().forEach(productCatalog -> {
            if (checkIfCatalogCanBeRemoved(productCatalog)) {
                final String catalogUid = configurationProvider.getProductCatalogId(productCatalog);
                logReporter.logInfo(context, String.format("Starting catalog %s removal", catalogUid));

                removeProductCatalog(catalogUid);

                logReporter.logInfo(context, String.format("Completed catalog %s removal", catalogUid));
            } else {
                logReporter.logInfo(context, String.format("Catalog %s cannot be removed, because it is brand or global product catalog", productCatalog), "red");
            }
        });
    }

    protected boolean checkIfCatalogCanBeRemoved(final ProductCatalogEnum catalog) {
        return true;
    }

    private void removeProductCatalog(final String catalogUid) {
        final RemoveCatalogVersionCronJobModel cronJob = createRemoveCatalogVersionCronJob(catalogUid);
        cronJobService.performCronJob(cronJob, true);
    }

    private RemoveCatalogVersionCronJobModel createRemoveCatalogVersionCronJob(final String catalogUid) {
        final CatalogModel catalog = catalogService.getCatalogForId(catalogUid);

        final RemoveCatalogVersionCronJobModel cronJob = modelService.create(RemoveCatalogVersionCronJobModel.class);
        cronJob.setJob(cronJobService.getJob("RemoveCatalogVersionJob"));
        cronJob.setCode(String.format("removeCatalogVersionJob_%s", catalog.getId()));
        cronJob.setCatalog(catalog);
        cronJob.setSessionUser(userService.getAdminUser());

        modelService.save(cronJob);
        return cronJob;
    }
}
