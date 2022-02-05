package com.github.mlytvyn.patches.groovy.context.impex;

import java.util.HashMap;
import java.util.Map;

public class ImpexTemplateContext {

    private final String description;
    private final Map<String, Object> macroParameters = new HashMap<>();

    private ImpexTemplateContext(final String description) {
        this.description = description;
    }

    public static ImpexTemplateContext of(final String description) {
        return new ImpexTemplateContext(description);
    }

    public Map<String, Object> macroParameters() {
        return macroParameters;
    }

    public ImpexTemplateContext macroParameter(final String name, final Object value) {
        macroParameters().put(name, value);
        return this;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return "ImpexTemplateContext{" +
                "description='" + description + '\'' +
                ", macroParameters=" + macroParameters +
                '}';
    }
}
