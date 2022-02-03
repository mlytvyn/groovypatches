package com.github.mlytvyn.patches.groovy.scripting.engine.impl;

import com.github.mlytvyn.patches.groovy.scripting.engine.ScriptingLanguagesService;
import com.google.common.base.Preconditions;
import de.hybris.platform.regioncache.key.CacheKey;
import de.hybris.platform.scripting.engine.exception.ScriptURIException;
import de.hybris.platform.scripting.engine.impl.DefaultScriptingLanguagesService;
import de.hybris.platform.scripting.engine.internal.cache.ScriptExecutablesCacheService;
import de.hybris.platform.scripting.engine.repository.CacheableScriptsRepository;
import de.hybris.platform.scripting.engine.repository.ScriptRepositoriesRegistry;
import de.hybris.platform.scripting.engine.repository.ScriptsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;

public class ExtendedScriptingLanguagesService extends DefaultScriptingLanguagesService implements ScriptingLanguagesService {

    private static final Logger LOG = LogManager.getLogger();

    @Resource(name = "scriptExecutableCacheService")
    private ScriptExecutablesCacheService cacheService;
    @Resource(name = "scriptRepositoriesRegistry")
    private ScriptRepositoriesRegistry scriptRepositoriesRegistry;

    /*
    mostly copy-paste from OOTB, except method for invalidating the cached script
     */
    @Override
    public void invalidateCachedScript(final String scriptURI) {
        LOG.trace("Getting executable by scriptURI [scriptURI: {}]", scriptURI);

        final String[] uriParts = splitScriptURI(scriptURI);
        final String protocol = uriParts[0];
        final String path = uriParts[1];
        final ScriptsRepository repository = scriptRepositoriesRegistry.getRepositoryByProtocol(protocol);
        if (repository == null) {
            throw new IllegalStateException("Repository must not be null");
        } else {
            final CacheableScriptsRepository cachingRepository = asCachingRepository(repository);
            if (cachingRepository != null) {
                final CacheKey cacheKey = cachingRepository.createCacheKey(protocol, path);
                cacheService.invalidate(cacheKey);
            }
        }
    }

    /*
    Copy-paste from OOTB
     */
    private CacheableScriptsRepository asCachingRepository(ScriptsRepository repository) {
        try {
            return (CacheableScriptsRepository) repository;
        } catch (final ClassCastException var2) {
            return null;
        }
    }

    /*
    Copy-paste from OOTB
     */
    private String[] splitScriptURI(String scriptURI) {
        try {
            String[] uriParts = scriptURI.split("://");
            validateUriParts(uriParts);
            uriParts[0] = uriParts[0].trim();
            uriParts[1] = uriParts[1].trim();
            return uriParts;
        } catch (Exception var3) {
            throw new ScriptURIException(var3.getMessage(), var3);
        }
    }

    /*
    Copy-paste from OOTB
     */
    private void validateUriParts(String[] uriParts) {
        Preconditions.checkNotNull(uriParts, "Split result for URI cannot be null");
        Preconditions.checkState(uriParts.length == 2, "Split result for URI must have 2 parts - protocol and path (ie. classpath://foo/bar)");
        Preconditions.checkState(StringUtils.isNotBlank(uriParts[0]), "Protocol part must not be empty");
        Preconditions.checkState(StringUtils.isNotBlank(uriParts[1]), "Path part must not be empty");
    }
}
