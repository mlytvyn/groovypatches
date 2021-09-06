

package com.github.mlytvyn.patches.groovy.scripting.engine;

public interface ScriptingLanguagesService extends de.hybris.platform.scripting.engine.ScriptingLanguagesService {

    void invalidateCachedScript(String scriptURI);
}
