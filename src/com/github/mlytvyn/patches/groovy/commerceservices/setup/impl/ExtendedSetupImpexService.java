package com.github.mlytvyn.patches.groovy.commerceservices.setup.impl;

import com.github.mlytvyn.patches.groovy.commerceservices.setup.SetupImpexService;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportConfig;
import com.github.mlytvyn.patches.groovy.context.impex.ImpexImportException;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupImpexService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class ExtendedSetupImpexService extends DefaultSetupImpexService implements SetupImpexService {

    private static final Logger LOG = LogManager.getLogger(ExtendedSetupImpexService.class);

    @Override
    public void importImpexFile(final String impexPath, final ImpexImportConfig impexImportConfig, final Map<String, Object> macroParameters) {
        try (final InputStream resourceAsStream = getClass().getResourceAsStream(impexPath)) {
            if (resourceAsStream == null) {
                if (impexImportConfig.errorIfMissing()) {
                    throw new ImpexImportException("Required Impex file is missing: " + impexPath);
                }

                LOG.info("Importing [{}]... SKIPPED (Optional File Not Found)", impexPath);
                return;
            }

            try (final InputStream stream = getMergedInputStream(macroParameters, resourceAsStream)) {
                importImpex(impexPath, impexImportConfig, stream);

                // Try to import language specific impex files
                if (impexPath.endsWith(getImpexExt())) {
                    importLanguageImpexes(impexPath, impexImportConfig, macroParameters);
                }
            }
        } catch (final IOException e) {
            throw new ImpexImportException(e.getMessage(), e);
        }
    }

    private void importImpex(final String impexPath, final ImpexImportConfig impexImportConfig, final InputStream stream) {
        try {
            LOG.info("Importing [{}]...", impexPath);

            final ImportConfig importConfig = new ImportConfig();
            importConfig.setScript(new StreamBasedImpExResource(stream, getFileEncoding()));
            importConfig.setEnableCodeExecution(impexImportConfig.enableCodeExecution());
            importConfig.setLegacyMode(impexImportConfig.legacyMode());
            importConfig.setFailOnError(impexImportConfig.failOnError());
            importConfig.setRemoveOnSuccess(impexImportConfig.removeOnSuccess());
            importConfig.setSynchronous(impexImportConfig.synchronous());
            importConfig.setLegacyMode(impexImportConfig.legacyMode());

            final ImportResult importResult = getImportService().importData(importConfig);
            if (importResult.isError()) {
                if (impexImportConfig.failOnError()) {
                    throw new ImpexImportException("Non-failing impex importing [" + impexPath + "]... FAILED");
                } else {
                    LOG.error("Importing [" + impexPath + "]... FAILED");
                }
            }
        } catch (final Exception e) {
            if (impexImportConfig.failOnError()) {
                throw new ImpexImportException("Non-failing impex importing [" + impexPath + "]... FAILED", e);
            } else {
                LOG.error("Importing [" + impexPath + "]... FAILED");
            }
        }
    }

    private void importLanguageImpexes(final String impexPath, final ImpexImportConfig impexImportConfig, final Map<String, Object> macroParameters) {
        final String filePath = impexPath.substring(0, impexPath.length() - getImpexExt().length());

        getCommonI18NService().getAllLanguages().forEach(language -> {
            final String languageFilePath = filePath + "_" + language.getIsocode() + getImpexExt();
            try (final InputStream languageResourceAsStream = getClass().getResourceAsStream(languageFilePath)) {
                if (languageResourceAsStream != null) {
                    try (var mergeInputStream = getMergedInputStream(macroParameters, languageResourceAsStream)) {
                        importImpex(languageFilePath, impexImportConfig, mergeInputStream);
                    }
                }
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

    protected String buildMacroHeader(final Map<String, Object> macroParameters) {
        // no pun intended with this method name
        final StringBuilder builder = new StringBuilder();

        for (final Map.Entry<String, Object> entry : macroParameters.entrySet()) {
            final String macroName = entry.getKey().charAt(0) == '$'
                    ? entry.getKey()
                    : '$' + entry.getKey();

            final String val = Optional.ofNullable(entry.getValue())
                    .map(String::valueOf)
                    .orElse(StringUtils.EMPTY);

            builder.append(macroName).append("=").append(val).append("\n");
        }
        return builder.toString();
    }

    protected InputStream getMergedInputStream(final Map<String, Object> macroParameters, final InputStream fileStream) {
        if (macroParameters != null && !macroParameters.isEmpty()) {
            final String header = buildMacroHeader(macroParameters);
            return new SequenceInputStream(IOUtils.toInputStream(header, StandardCharsets.UTF_8), fileStream);
        } else {
            return fileStream;
        }
    }
}
