

package com.github.mlytvyn.patches.groovy.util;

import de.hybris.platform.core.Registry;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinWorkerThread extends java.util.concurrent.ForkJoinWorkerThread {
    /**
     * Creates a ForkJoinWorkerThread operating in the given pool.
     *
     * @param pool the pool this thread works in
     * @throws NullPointerException if pool is null
     */
    public ForkJoinWorkerThread(final ForkJoinPool pool) {
        super(pool);
    }

    @Override
    protected void onStart() {
        Registry.activateMasterTenant();
    }
}
