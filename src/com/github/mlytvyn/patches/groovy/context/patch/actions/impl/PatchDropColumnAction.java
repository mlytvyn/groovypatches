package com.github.mlytvyn.patches.groovy.context.patch.actions.impl;

import com.github.mlytvyn.patches.groovy.context.DropColumnContext;
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriptor;
import com.github.mlytvyn.patches.groovy.context.patch.PatchException;
import com.github.mlytvyn.patches.groovy.context.patch.actions.PatchAction;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Utilities;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatchDropColumnAction implements PatchAction<PatchContextDescriptor> {

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "typeService")
    private TypeService typeService;

    @Override
    public void execute(final SystemSetupContext context, final PatchContextDescriptor patch) {
        if (CollectionUtils.isEmpty(patch.getDropColumnContexts())) return;

        logReporter.logInfo(context, "Drop table column started");
        patch.getDropColumnContexts().forEach(dropColumnContexts -> dropColumnsInternal(patch, dropColumnContexts));
        logReporter.logInfo(context, "Drop table column completed");
    }

    protected void dropColumnsInternal(final PatchContextDescriptor patch, final DropColumnContext dropColumnContext) throws PatchException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = Registry.getCurrentTenant().getDataSource().getConnection();
            final ComposedTypeModel composedType = typeService.getComposedTypeForClass(dropColumnContext.targetClass());
            final String table = composedType.getTable();
            final String databaseColumn = dropColumnContext.columnName();
            final String statement = String.format("ALTER TABLE %s DROP COLUMN %s;", table, databaseColumn);

            pstmt = conn.prepareStatement(statement);
            pstmt.execute();
        } catch (final SQLException | SystemException e) {
            throw new PatchException(patch, String.format("Cannot drop '%s' column '%s' type", dropColumnContext.targetClass().getTypeName(), dropColumnContext.columnName()), e);
        } finally {
            Utilities.tryToCloseJDBC(conn, pstmt, null);
        }
    }

}
