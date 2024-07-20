package com.github.mlytvyn.patches.groovy.context;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample usage:
 * <pre>{@code
 * ChangeFieldTypeContext.of(ProductModel.class, ProductModel.NAME)
 *                       .dbFieldType(Config.DatabaseName.HANA, "TEXT")
 *                       .dbFieldType(Config.DatabaseName.MYSQL, "TEXT")
 *                       .dbFieldType(Config.DatabaseName.SQLSERVER, "NCLOB")
 * }</pre>
 */
public class ChangeFieldTypeContext {

    private final Class<? extends ItemModel> targetClass;
    private final String fieldName;
    private final Map<Config.DatabaseName, String> dbFieldTypes = new HashMap<>();

    private ChangeFieldTypeContext(final Class<? extends ItemModel> targetClass, final String fieldName) {
        this.targetClass = targetClass;
        this.fieldName = fieldName;
    }

    public static ChangeFieldTypeContext of(final Class<? extends ItemModel> targetClass, final String fieldName) {
        return new ChangeFieldTypeContext(targetClass, fieldName);
    }

    public Class<? extends ItemModel> targetClass() {
        return targetClass;
    }

    public String fieldName() {
        return fieldName;
    }

    public Map<Config.DatabaseName, String> dbFieldTypes() {
        return dbFieldTypes;
    }

    public ChangeFieldTypeContext dbFieldType(final Config.DatabaseName databaseName, final String fieldType) {
        dbFieldTypes().put(databaseName, fieldType);
        return this;
    }
}
