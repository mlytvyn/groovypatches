package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;

public class PatchBeforeConsumerAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        patch.getBeforeConsumer()
            .ifPresent(beforeConsumer -> {
                logReporter.logInfo(context, "Started custom logic BEFORE patch", "blue");

                beforeConsumer.accept(context);

                logReporter.logInfo(context, "Completed custom logic BEFORE patch ", "blue");
            });
    }

}
