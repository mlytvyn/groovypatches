package com.github.mlytvyn.patches.groovy.context.release.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.actions.ReleaseContextAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class ReleaseContextValidateAction implements ReleaseContextAction {

    @Resource(name = "patchValidateAction")
    private PatchAction<PatchContextDescriptor> patchValidateAction;

    @Override
    public void execute(final SystemSetupContext context, final ReleaseContext release) {
        release.patches().forEach(patch -> patchValidateAction.execute(context, patch));
    }

}
