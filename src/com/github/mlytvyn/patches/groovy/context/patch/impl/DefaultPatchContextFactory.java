package com.github.mlytvyn.patches.groovy.context.patch.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextFactory;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import javax.annotation.Resource;

public class DefaultPatchContextFactory implements PatchContextFactory<GlobalContext, ReleaseContext, PatchContext<GlobalContext, ReleaseContext>> {

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Override
    public PatchContext<GlobalContext, ReleaseContext> createContext(final GlobalContext globalContext, final ReleaseContext release, final String patchNumber, final String patchId) {
        return new PatchContext<>(globalContext, release, configurationService.getConfiguration().getString("patches.groovy.project.extension.name"), patchNumber, patchId);
    }
}
