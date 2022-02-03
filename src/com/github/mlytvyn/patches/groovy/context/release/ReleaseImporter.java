package com.github.mlytvyn.patches.groovy.context.release;

import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.List;

public interface ReleaseImporter {

    void execute(SystemSetupContext context, List<ReleaseContext> releases);
}
