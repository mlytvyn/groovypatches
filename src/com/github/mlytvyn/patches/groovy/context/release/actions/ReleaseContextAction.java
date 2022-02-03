package com.github.mlytvyn.patches.groovy.context.release.actions;

import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import de.hybris.platform.core.initialization.SystemSetupContext;

public interface ReleaseContextAction {

    void execute(SystemSetupContext context, ReleaseContext releaseContext);
}
