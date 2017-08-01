/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import ro.polak.http.ServerConfig;
import ro.polak.http.ServerConfigFactory;
import ro.polak.http.impl.ServerConfigImpl;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.resource.provider.impl.FileResourceProvider;
import ro.polak.http.resource.provider.impl.ServletResourceProvider;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.session.storage.FileSessionStorage;

/**
 * Default server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class DefaultServerConfigFactory implements ServerConfigFactory {

    private static final Logger LOGGER = Logger.getLogger(DefaultServerConfigFactory.class.getName());

    @Override
    public ServerConfig getServerConfig() {
        String baseConfigPath;
        baseConfigPath = getBasePath();

        return getServerConfig(baseConfigPath);
    }

    /**
     * This method is designed to be overloaded.
     *
     * @return
     */
    protected String getTempPath() {
        return System.getProperty("java.io.tmpdir") + File.separator + "webserver" + File.separator;
    }

    /**
     * This method is designed to be overloaded.
     *
     * @return
     */
    protected String getBasePath() {
        return "." + File.separator + "httpd" + File.separator;
    }

    /**
     * This method is designed to be overloaded.
     *
     * @return
     */
    protected Map<String, Object> getAdditionalServletContextAttributes() {
        return new HashMap<>();
    }

    /**
     * This method is designed to be overloaded.
     *
     * @return
     */
    protected Set<ResourceProvider> getAdditionalResourceProviders(ServerConfig serverConfig) {
        return new HashSet<>();
    }

    private ServerConfig getServerConfig(String baseConfigPath) {
        ServerConfigImpl serverConfig;

        String tempPath = getTempPath();

        try {
            serverConfig = ServerConfigImpl.createFromPath(baseConfigPath, tempPath);
        } catch (IOException e) {
            LOGGER.warning("Unable to read server config. Using the default configuration. " + e.getMessage());
            serverConfig = new ServerConfigImpl(tempPath);
        }

        serverConfig.setResourceProviders(selectActiveResourceProviders(serverConfig));
        return serverConfig;
    }

    private ServletContextWrapper getServletContext(ServerConfig serverConfig) {
        ServletContextWrapper servletContext = new ServletContextWrapper(serverConfig,
                new FileSessionStorage(serverConfig.getTempPath()));

        servletContext.setAttribute(ServerConfig.class.getName(), serverConfig);

        for (Map.Entry<String, Object> entry : getAdditionalServletContextAttributes().entrySet()) {
            servletContext.setAttribute(entry.getKey(), entry.getValue());
        }

        return servletContext;
    }


    /**
     * For performance reasons ServletResourceProvider is the last resource provider.
     *
     * @param serverConfig
     * @return
     */
    private ResourceProvider[] selectActiveResourceProviders(ServerConfig serverConfig) {
        List<ResourceProvider> resourceProviders = new ArrayList<>();

        resourceProviders.add(getFileResourceProvider(serverConfig));

        for (ResourceProvider resourceProvider : getAdditionalResourceProviders(serverConfig)) {
            resourceProviders.add(resourceProvider);
        }

        resourceProviders.add(getServletResourceProvider(serverConfig));
        return resourceProviders.toArray(new ResourceProvider[resourceProviders.size()]);
    }

    private FileResourceProvider getFileResourceProvider(ServerConfig serverConfig) {
        return new FileResourceProvider(serverConfig.getMimeTypeMapping(),
                serverConfig.getDocumentRootPath());
    }

    private ServletResourceProvider getServletResourceProvider(ServerConfig serverConfig) {
        return new ServletResourceProvider(
                getServletContext(serverConfig),
                serverConfig.getServletMappedExtension());
    }
}
