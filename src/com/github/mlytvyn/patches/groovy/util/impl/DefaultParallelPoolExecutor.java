package com.github.mlytvyn.patches.groovy.util.impl;

import com.github.mlytvyn.patches.groovy.util.LogReporter;
import com.github.mlytvyn.patches.groovy.util.ForkJoinWorkerThread;
import com.github.mlytvyn.patches.groovy.util.ParallelPoolExecutor;
import de.hybris.platform.core.initialization.SystemSetupContext;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class DefaultParallelPoolExecutor implements ParallelPoolExecutor {

    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public Consumer<Runnable> execute(final SystemSetupContext context) {
        return (Runnable task) -> execute(Runtime.getRuntime().availableProcessors(), task, getUncaughtExceptionHandler(context), getExceptionConsumer(context));
    }

    protected void execute(final int parallelism, final Runnable task, final Thread.UncaughtExceptionHandler handler, Consumer<Exception> submitExceptionHandler) {
        final ForkJoinPool pool = getForkJoinPool(parallelism, handler);
        try {
            pool.submit(task).get();
        } catch (final InterruptedException | ExecutionException e) {
            submitExceptionHandler.accept(e);
        } finally {
            pool.shutdown();
        }
    }

    private ForkJoinPool getForkJoinPool(final int parallelism, final Thread.UncaughtExceptionHandler handler) {
        return new ForkJoinPool(parallelism, ForkJoinWorkerThread::new, handler, true);
    }

    private Thread.UncaughtExceptionHandler getUncaughtExceptionHandler(final SystemSetupContext context) {
        return (t, e) -> logReporter.logError(context, e.getMessage(), e);
    }

    private Consumer<Exception> getExceptionConsumer(final SystemSetupContext context) {
        return e -> logReporter.logError(context, e.getMessage(), e);
    }
}
