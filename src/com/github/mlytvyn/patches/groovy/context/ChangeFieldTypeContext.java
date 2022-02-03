package com.github.mlytvyn.patches.groovy.context;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.util.Config;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * Sample usage:
 * <pre>{@code
 * ChangeFieldTypeContext.builder(ProductModel.class, ProductModel.NAME)
 *                         .dbFieldType(Config.DatabaseName.HANA, "TEXT")
 *                         .dbFieldType(Config.DatabaseName.MYSQL, "TEXT")
 *                         .dbFieldType(Config.DatabaseName.SQLSERVER, "NCLOB")
 *                         .build()
 * }</pre>
 */
@Data
@Accessors(chain = true, fluent = true)
@Builder(builderMethodName = "internalBuilder")
@RequiredArgsConstructor
public class ChangeFieldTypeContext {
    @NonNull
    private final Class<? extends ItemModel> targetClass;
    @NonNull
    private final String fieldName;
    @Singular
    private final Map<Config.DatabaseName, String> dbFieldTypes;

    public static ChangeFieldTypeContextBuilder builder(final Class<? extends ItemModel> targetClass, final String fieldName) {
        return internalBuilder().targetClass(targetClass).fieldName(fieldName);
    }

}
