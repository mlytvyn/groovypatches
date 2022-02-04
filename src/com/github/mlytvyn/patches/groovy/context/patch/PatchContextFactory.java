package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import org.springframework.security.access.method.P;

public interface PatchContextFactory<G extends GlobalContext, R extends ReleaseContext, P extends PatchContext<G, R>> {

    P createContext(G globalContext, R release, String patchNumber, String patchId);
}
