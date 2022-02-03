package com.github.mlytvyn.patches.groovy.context;

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
public class ImpexContext {

    @NonNull
    private final String name;
    @Singular
    private final Map<String, Object> macroParameters;

    public static ImpexContextBuilder builder(final String name) {
        return internalBuilder().name(name);
    }

}
