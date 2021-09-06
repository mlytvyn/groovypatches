

package com.github.mlytvyn.patches.groovy.context.release;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;

import java.io.IOException;

public interface ReleasesCollector<G extends GlobalContext> {

    void collect(G globalContext, String locationPattern) throws IOException;
}
