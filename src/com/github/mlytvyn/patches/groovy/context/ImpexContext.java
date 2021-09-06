

package com.github.mlytvyn.patches.groovy.context;

import java.util.HashMap;
import java.util.Map;

public class ImpexContext {

    private final String name;
    private final Map<String, Object> macroParameters = new HashMap<>();

    private ImpexContext(final String name) {
        this.name = name;
    }

    public static ImpexContext prepare(final String name) {
        return new ImpexContext(name);
    }

    public ImpexContext put(final String key, final String value) {
        macroParameters.put(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getMacroParameters() {
        return macroParameters;
    }
}
