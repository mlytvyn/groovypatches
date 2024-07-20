package com.github.mlytvyn.patches.groovy.context;

import de.hybris.platform.core.model.ItemModel;

import javax.validation.constraints.NotNull;

/**
 * Sample usage:
 * <pre>{@code
 * ChangeFieldTypeContext.of(ProductModel.class, ProductModel.NAME)
 * }</pre>
 */
public class DropColumnContext {

    private final Class<? extends ItemModel> targetClass;
    private final String columnName;

    private DropColumnContext(@NotNull final Class<? extends ItemModel> targetClass, @NotNull final String columnName) {
        this.targetClass = targetClass;
        this.columnName = columnName;
    }

    public static DropColumnContext of(@NotNull final Class<? extends ItemModel> targetClass, @NotNull final String columnName) {
        return new DropColumnContext(targetClass, columnName);
    }

    @NotNull
    public Class<? extends ItemModel> targetClass() {
        return targetClass;
    }

    @NotNull
    public String columnName() {
        return columnName;
    }

}
