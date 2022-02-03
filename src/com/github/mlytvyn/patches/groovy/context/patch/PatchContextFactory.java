package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;

public interface PatchContextFactory<G extends GlobalContext, P extends PatchContext<G>> {

    P createContext(G globalContext, ReleaseContext release, String patchNumber, String patchId);
}
