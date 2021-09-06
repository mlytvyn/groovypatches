

package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class PatchAfterConsumerAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        patch.getAfterConsumer()
            .ifPresent(afterConsumer -> {
                logReporter.logInfo(context, "Started custom logic AFTER patch", "blue");

                afterConsumer.accept(context);

                logReporter.logInfo(context, "Completed custom logic AFTER patch", "blue");
            });
    }

}
