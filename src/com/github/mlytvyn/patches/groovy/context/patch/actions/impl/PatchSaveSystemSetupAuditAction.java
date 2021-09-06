

package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.initialization.SystemSetupAuditModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

public class PatchSaveSystemSetupAuditAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "userService")
    protected UserService userService;
    @Resource(name = "modelService")
    protected ModelService modelService;
    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        final SystemSetupAuditModel systemPatch = modelService.create(SystemSetupAuditModel.class);
        systemPatch.setHash(patch.hash());
        systemPatch.setDescription(patch.getDescription());
        systemPatch.setName(patch.getName());
        systemPatch.setUser(userService.getCurrentUser());
        systemPatch.setClassName(patch.getReleaseContext().getId());
        systemPatch.setMethodName(patch.getId());
        systemPatch.setRequired(true);
        systemPatch.setExtensionName(configurationService.getConfiguration().getString("patches.groovy.project.extension.name"));
        modelService.save(systemPatch);
    }

}
