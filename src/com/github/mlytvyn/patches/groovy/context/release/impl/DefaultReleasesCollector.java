package com.github.mlytvyn.patches.groovy.context.release.impl;

import com.github.mlytvyn.patches.groovy.context.GroovyPatchContextService;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchesCollector;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.context.release.ReleasesCollector;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultReleasesCollector implements ReleasesCollector<GlobalContext> {

    private static final Logger LOG = LogManager.getLogger(DefaultReleasesCollector.class);
    @Resource(name = "configurationService")
    protected ConfigurationService configurationService;
    @Resource(name = "patchesCollector")
    protected PatchesCollector<GlobalContext> patchesCollector;
    @Resource(name = "groovyPatchContextService")
    protected GroovyPatchContextService groovyPatchContextService;

    @Override
    public void collect(final GlobalContext globalContext, final String locationPattern) throws IOException {
        final Map<String, ReleaseContext> cachedReleases = new HashMap<>();

        final List<ReleaseContext> releases = Stream.of(new PathMatchingResourcePatternResolver(this.getClass().getClassLoader()).getResources(locationPattern))
            .filter(org.springframework.core.io.Resource::isFile)
            .filter(org.springframework.core.io.Resource::isReadable)
            .map(resource -> {
                try {
                    return resource.getURL().toString();
                } catch (final IOException e) {
                    LOG.warn(e.getMessage(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(fullPath -> fullPath.substring(fullPath.indexOf(getReleasesHome())))
            .sorted(Comparator.comparing(shortPath -> shortPath))
            .map(shortPath -> shortPath.substring(getReleasesHome().length()).split("/", 3))
            .collect(Collectors.groupingBy(
                // Release id: <Release number>, 20210512
                version2release2patch -> getReleaseContext(version2release2patch, cachedReleases),
                LinkedHashMap::new,
                // patches represented as: <patch number>_<JIRA project>-<JIRA patch number>, 0002_PRJ-1221
                // `.groovy` file extension will be removed
                Collectors.mapping(version2release2patch -> version2release2patch[2].substring(0, version2release2patch[2].indexOf(".groovy")), Collectors.toList()))
            )
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                // release = entry.getKey()
                // plain patches = entry.getValue()
                entry -> patchesCollector.collect(globalContext, entry.getKey(), entry.getValue()),
                (o1, o2) -> o1,
                LinkedHashMap::new
            ))
            .entrySet().stream()
            // once collected we have to filter out releases without any patches
            .filter(Predicate.not(entry -> entry.getValue().isEmpty()))
            .peek(entry -> entry.getKey().patches(entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        globalContext.releases(releases);
    }

    private String getReleasesHome() {
        return configurationService.getConfiguration().getString("patches.groovy.project.extension.name") + "/releases/";
    }

    /**
     * Performance improvement, we have to cache already identified releases, otherwise it will be created for every single patch and then added or not to the grouped by map
     */
    private ReleaseContext getReleaseContext(final String[] version2release2patch, final Map<String, ReleaseContext> cachedReleases) {
        final String version = version2release2patch[0];
        final String id = version2release2patch[1];

        return cachedReleases.computeIfAbsent(version + id, s -> groovyPatchContextService.restoreOrCreateReleaseContext(version, id));
    }
}
