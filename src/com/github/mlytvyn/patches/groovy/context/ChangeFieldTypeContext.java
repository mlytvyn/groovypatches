

package com.github.mlytvyn.patches.groovy.context;

import de.hybris.platform.util.Config;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ChangeFieldTypeContext {
    private final String fieldName;
    private final String tableName;
    private Map<Config.DatabaseName, ChangeFieldTypeContext> databaseSpecificContexts;
    private String newFieldType;

    private ChangeFieldTypeContext(final String fieldName, final String typecode) {
        this.fieldName = fieldName;
        this.tableName = typecode;
        this.databaseSpecificContexts = new HashMap<>();
    }

    private ChangeFieldTypeContext(final String fieldName, final String tableName, final String newFieldType) {
        if (StringUtils.isNotEmpty(fieldName)) {
            this.fieldName = fieldName;
        } else {
            throw new IllegalArgumentException("Field Name must not be empty");
        }
        if (StringUtils.isNotEmpty(tableName)) {
            this.tableName = tableName;
        } else {
            throw new IllegalArgumentException("Table Name must not be empty");
        }
        if (StringUtils.isNotEmpty(newFieldType)) {
            this.newFieldType = newFieldType;
        } else {
            throw new IllegalArgumentException("New Field Type must not be empty");
        }
    }

    /**
     * @param fieldName will be used in log message if target database not specified
     * @param typecode  will be used in log message if target database not specified
     */
    public static ChangeFieldTypeContext prepare(final String fieldName, final String typecode) {
        return new ChangeFieldTypeContext(fieldName, typecode);
    }

    public ChangeFieldTypeContext add(final Config.DatabaseName database, final String fieldName, final String tableName, final String newFieldType) {
        if (database == null) {
            throw new IllegalArgumentException("Valid Database must be specified");
        }
        databaseSpecificContexts.put(database, new ChangeFieldTypeContext(fieldName, tableName, newFieldType));
        return this;
    }

    public ChangeFieldTypeContext mysql(final String fieldName, final String tableName, final String newFieldType) {
        add(Config.DatabaseName.MYSQL, fieldName, tableName, newFieldType);
        return this;
    }

    public ChangeFieldTypeContext hana(final String fieldName, final String tableName, final String newFieldType) {
        add(Config.DatabaseName.HANA, fieldName, tableName, newFieldType);
        return this;
    }

    public ChangeFieldTypeContext mssql(final String fieldName, final String tableName, final String newFieldType) {
        add(Config.DatabaseName.SQLSERVER, fieldName, tableName, newFieldType);
        return this;
    }

    public Map<Config.DatabaseName, ChangeFieldTypeContext> getDatabaseSpecificContexts() {
        return databaseSpecificContexts;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getNewFieldType() {
        return newFieldType;
    }
}
