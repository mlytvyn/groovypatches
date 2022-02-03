package com.github.mlytvyn.patches.groovy.util;

import de.hybris.platform.core.initialization.SystemSetupContext;

import java.util.function.Consumer;

public interface ParallelPoolExecutor {

    Consumer<Runnable> execute(SystemSetupContext context);
}
