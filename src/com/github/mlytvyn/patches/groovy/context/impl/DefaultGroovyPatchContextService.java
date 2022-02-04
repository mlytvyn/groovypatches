package com.github.mlytvyn.patches.groovy.context.impl;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import com.github.mlytvyn.patches.groovy.context.global.GlobalContextFactory;
import com.github.mlytvyn.patches.groovy.context.release.ReleaseContext;
import com.github.mlytvyn.patches.groovy.EnvironmentEnum;
import com.github.mlytvyn.patches.groovy.context.ContextSerializationException;
import com.github.mlytvyn.patches.groovy.context.CurrentEnvironmentProvider;
import com.github.mlytvyn.patches.groovy.context.GroovyPatchContextService;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

public class DefaultGroovyPatchContextService implements GroovyPatchContextService {

    private static final Logger LOG = LogManager.getLogger();

    @Resource(name = "currentEnvironmentProvider")
    protected CurrentEnvironmentProvider currentEnvironmentProvider;
    @Resource(name = "mediaService")
    private MediaService mediaService;
    @Resource(name = "modelService")
    private ModelService modelService;
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;
    @Resource(name = "groovyGlobalContextFactory")
    private GlobalContextFactory<GlobalContext> groovyGlobalContextFactory;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GlobalContext> T restoreOrCreateGlobalContext() throws ContextSerializationException {
        return (T) restoreOrCreateContext(getGlobalContextMediaId(), () -> {
            final EnvironmentEnum currentEnvironment = currentEnvironmentProvider.getCurrentEnvironment();
            return groovyGlobalContextFactory.createContext(currentEnvironment);
        });
    }

    @Override
    public <T extends GlobalContext> void serializeGlobalContext(final T globalContext) throws ContextSerializationException {
        serializeContext(globalContext, getGlobalContextMediaId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends ReleaseContext> T restoreOrCreateReleaseContext(final String releaseVersion, final String releaseId) throws ContextSerializationException {
        return (T) restoreOrCreateContext(getReleaseContextMediaID(releaseVersion, releaseId), () -> new ReleaseContext(releaseVersion, releaseId));
    }

    @Override
    public <T extends ReleaseContext> void serializeReleaseContext(final T releaseContext) throws ContextSerializationException {
        serializeContext(releaseContext, getReleaseContextMediaID(releaseContext.version(), releaseContext.id()));
    }

    private String getGlobalContextMediaId() {
        return configurationService.getConfiguration().getString("patches.groovy.project.extension.name") + "_GlobalContext";
    }

    private String getReleaseContextMediaID(final String version, final String id) {
        return configurationService.getConfiguration().getString("patches.groovy.project.extension.name") + "_ReleaseContext_" + version + "_" + id;
    }

    @SuppressWarnings("unchecked")
    private <T> T restoreOrCreateContext(final String mediaID, final Supplier<T> supplier) throws ContextSerializationException {
        try {
            final MediaModel media = mediaService.getMedia(mediaID);
            final byte[] bytes = mediaService.getDataFromMedia(media);

            try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 final ObjectInputStream in = new ObjectInputStream(bis)) {
                LOG.warn("Previous failed patching execution found, it will be restored and reapplied [context: {}]", mediaID);
                final T context = (T) in.readObject();
                // once restored - delete it from DB
                modelService.remove(media);
                LOG.warn("Found failed patching execution removed [context: {}]", mediaID);
                return context;
            }
        } catch (final UnknownIdentifierException e) {
            LOG.trace("Previous failed patching execution not found, new context can be created [context: {}]", mediaID);
            return supplier.get();
        } catch (final IOException | ClassNotFoundException e) {
            throw new ContextSerializationException(String.format("Something went wrong, could not restore context from previous patching execution [media ID: %s]", mediaID), e);
        }
    }

    private void serializeContext(final Object object, final String mediaID) throws ContextSerializationException {
        try {
            final CatalogUnawareMediaModel media = modelService.create(CatalogUnawareMediaModel.class);
            media.setCode(mediaID);
            modelService.save(media);

            try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 final ObjectOutputStream out = new ObjectOutputStream(bos)) {
                out.writeObject(object);
                mediaService.setDataForMedia(media, bos.toByteArray());
            }
            LOG.warn("Serialized patching context for next execution [context: {}]", mediaID);
        } catch (final IOException e) {
            throw new ContextSerializationException(e.getMessage(), e);
        }
    }

}
