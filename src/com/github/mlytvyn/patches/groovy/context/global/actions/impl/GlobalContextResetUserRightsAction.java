package com.github.mlytvyn.patches.groovy.context.global.actions.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.actions.GlobalContextAction;
import com.github.mlytvyn.patches.groovy.util.LogReporter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.persistence.security.ACLEntryJDBC;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.Utilities;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GlobalContextResetUserRightsAction implements GlobalContextAction<GlobalContext> {

    protected static final String FIND_PRINCIPAL_BY_PK = "SELECT {PK} FROM {Principal} WHERE {uid} = ?uid";

    @Resource(name = "logReporter")
    private LogReporter logReporter;
    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;

    @Override
    public void execute(final SystemSetupContext context, final GlobalContext globalContext) {
        final Set<String> principalUIDs = globalContext.resetUserRightsForPrincipals();
        if (principalUIDs.isEmpty()) return;

        logReporter.logInfo(context, "[Global] Started User Rights reset");
        logReporter.logInfo(context, "User Rights will be reset for: " + principalUIDs);

        final Set<PK> principalPKs = principalUIDs.stream()
                .filter(StringUtils::isNotBlank)
                .map(uid -> {
                    final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(FIND_PRINCIPAL_BY_PK);
                    flexibleSearchQuery.addQueryParameter("uid", uid);

                    final List<PrincipalModel> principals = flexibleSearchService.<PrincipalModel>search(flexibleSearchQuery).getResult();

                    if (principals.isEmpty()) {
                        logReporter.logWarn(context, String.format("Unable to find principal with uid '%s' to reset UserRights.", uid));
                    }

                    return principals;
                })
                .flatMap(Collection::stream)
                .map(AbstractItemModel::getPk)
                .collect(Collectors.toSet());

        resetUserRights(context, principalPKs);

        logReporter.logInfo(context, "[Global] Completed User Rights reset");
    }

    protected void resetUserRights(final SystemSetupContext context, final Set<PK> principalPKs) {
        if (principalPKs.isEmpty()) {
            logReporter.logWarn(context, "No existing principals were found.");

            return;
        }

        final String ugPKs = principalPKs.stream()
                .map(PK::getLongValueAsString)
                .collect(Collectors.joining(","));

        final String tableName = ACLEntryJDBC.ACLENTRIES_TABLE();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = Registry.getCurrentTenant().getDataSource().getConnection();
            pstmt = conn.prepareStatement("DELETE FROM " + tableName + " where principalpk in (?);");
            pstmt.setString(1, ugPKs);
            pstmt.execute();
        } catch (final Exception e) {
            logReporter.logError(context, "Error occurred during User Rights reset", e);
        } finally {
            Utilities.tryToCloseJDBC(conn, pstmt, null);
        }
    }

}
