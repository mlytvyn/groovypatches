package com.github.mlytvyn.patches.groovy.context.patch.actions;

import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import de.hybris.platform.core.initialization.SystemSetupContext;

public interface PatchAction<P extends PatchContextDescriptor> {

    void execute(SystemSetupContext context, P patch);
}
