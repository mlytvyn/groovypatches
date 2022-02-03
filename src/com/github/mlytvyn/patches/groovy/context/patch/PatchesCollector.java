package com.github.mlytvyn.patches.groovy.context.patch;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;

import java.util.LinkedHashSet;
import java.util.List;

public interface PatchesCollector<G extends GlobalContext> {

    LinkedHashSet<PatchContextDescriptor> collect(G globalContext, ReleaseContext release, List<String> plainPatches);
}
