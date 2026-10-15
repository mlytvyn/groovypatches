package com.github.mlytvyn.patches.groovy.context.patch;

/**
 * Specifies the folder the custom patch data folder is resolved against,
 * see {@link PatchContextDescriber#customPatchDataFolder(java.nio.file.Path, PatchDataFolderRelation)}.
 */
public enum PatchDataFolderRelation {

    /**
     * Custom patch data folder is resolved against the root patch data folder:
     * <p>{@code patchdata/[release]/[patch]} becomes {@code patchdata/[customPath]}</p>
     * <p>
     * Use it to share the same ImpEx files between patches of different releases.
     */
    ROOT,

    /**
     * Custom patch data folder is resolved against the release folder of the Patch, this is the default relation:
     * <p>{@code patchdata/[release]/[patch]} becomes {@code patchdata/[release]/[customPath]}</p>
     * <p>
     * Use it to share the same ImpEx files between patches of the same release.
     */
    RELEASE
}
