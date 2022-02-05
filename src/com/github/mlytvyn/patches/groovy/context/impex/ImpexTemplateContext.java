package com.github.mlytvyn.patches.groovy.context.impex;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true, fluent = true)
@Builder(builderMethodName = "internalBuilder")
@RequiredArgsConstructor
public class ImpexTemplateContext {

    @NonNull
    private final String description;
    @Singular
    private final Map<String, Object> macroParameters;

    public static ImpexTemplateContextBuilder builder(final String name) {
        return internalBuilder().description(name);
    }

}
