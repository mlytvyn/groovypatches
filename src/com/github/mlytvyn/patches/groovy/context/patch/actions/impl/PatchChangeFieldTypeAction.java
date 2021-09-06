

package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;
import org.fest.util.Collections;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatchChangeFieldTypeAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        if (!Collections.isEmpty(patch.getChangeFieldTypeContexts())) {
            logReporter.logInfo(context, "Change field type started");
            patch.getChangeFieldTypeContexts().forEach(changeFieldTypeContext -> changeFieldTypeInternal(patch, changeFieldTypeContext));
            logReporter.logInfo(context, "Change field type completed");
        }
    }

    protected void changeFieldTypeInternal(final PatchContextDescriptor patch, final ChangeFieldTypeContext changeFieldTypeContext) throws PatchException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = Registry.getCurrentTenant().getDataSource().getConnection();
            final String statement;
            final Config.DatabaseName databaseName = Config.getDatabaseName();
            final ChangeFieldTypeContext dbFieldContext = changeFieldTypeContext.getDatabaseSpecificContexts().get(databaseName);
            if (dbFieldContext == null) {
                // not configured for target DB
                return;
            }
            switch (databaseName) {
                case MYSQL:
                    statement = String.format("ALTER TABLE %s MODIFY %s %s;", dbFieldContext.getTableName(), dbFieldContext.getFieldName(), dbFieldContext.getNewFieldType());
                    break;
                case HANA:
                    statement = String.format("ALTER TABLE %s ALTER(%s %s);", dbFieldContext.getTableName(), dbFieldContext.getFieldName(), dbFieldContext.getNewFieldType());
                    break;
                case SQLSERVER:
                    statement = String.format("ALTER TABLE %s ALTER COLUMN %s %s;", dbFieldContext.getTableName(), dbFieldContext.getFieldName(), dbFieldContext.getNewFieldType());
                    break;
                default:
                    throw new PatchException(patch, "Unsupported DB Server. DB: " + Config.getDatabase());
            }
            pstmt = conn.prepareStatement(statement);
            pstmt.execute();
        } catch (final SQLException e) {
            throw new PatchException(patch, String.format("Cannot change '%s' column '%s' type", changeFieldTypeContext.getTableName(), changeFieldTypeContext.getFieldName()), e);
        } finally {
            Utilities.tryToCloseJDBC(conn, pstmt, null);
        }
    }
}
