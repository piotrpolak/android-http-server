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
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import example.Chunked;
import example.ChunkedWithDelay;
import example.Cookies;
import example.Forbidden;
import example.ForbiddenByFilter;
import example.Index;
import example.InternalServerError;
import example.NotFound;
import example.Session;
import example.Streaming;
import example.filter.FakeSecuredFilter;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.ServerConfigFactory;
import ro.polak.http.configuration.DeploymentDescriptorBuilder;
import ro.polak.http.configuration.impl.ServerConfigImpl;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.resource.provider.impl.FileResourceProvider;
import ro.polak.http.resource.provider.impl.ServletResourceProvider;
import ro.polak.http.servlet.impl.ServletContainerImpl;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.servlet.impl.ServletContextImpl;
import ro.polak.http.session.storage.FileSessionStorage;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Default server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class DefaultServerConfigFactory implements ServerConfigFactory {

    private static final Logger LOGGER = Logger.getLogger(DefaultServerConfigFactory.class.getName());
    /**
     * {@inheritDoc}
     */
    @Override
    public ServerConfig getServerConfig() {
        return getServerConfig(getBasePath());
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
    protected Set<ResourceProvider> getAdditionalResourceProviders(final ServerConfig serverConfig) {
        return new HashSet<>();
    }

    /**
     * Returns servlet context builder.
     *
     * @return
     */
    protected DeploymentDescriptorBuilder getDeploymentDescriptorBuilder(
            final SessionStorage sessionStorage, final ServerConfig serverConfig) {

        return DeploymentDescriptorBuilder.create()
                .withSessionStorage(sessionStorage)
                .withServerConfig(serverConfig)
                .addServletContext()
                    .withContextPath("/example")
                    .addFilter()
                        .withUrlPattern(Pattern.compile("^/secured/.*$"))
                        .withFilterClass(FakeSecuredFilter.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Chunked$"))
                        .withServletClass(Chunked.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/ChunkedWithDelay$"))
                        .withServletClass(ChunkedWithDelay.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Cookies$"))
                        .withServletClass(Cookies.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Forbidden$"))
                        .withServletClass(Forbidden.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Index$"))
                        .withServletClass(Index.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/$"))
                        .withServletClass(Index.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/InternalServerError$"))
                        .withServletClass(InternalServerError.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/NotFound$"))
                        .withServletClass(NotFound.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Session$"))
                        .withServletClass(Session.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Streaming$"))
                        .withServletClass(Streaming.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/secured/ForbiddenByFilter"))
                        .withServletClass(ForbiddenByFilter.class)
                    .end()
                .end();
    }

    private ServerConfig getServerConfig(final String baseConfigPath) {
        ServerConfigImpl serverConfig;

        String tempPath = getTempPath();
        String basePath = File.separator + "httpd" + File.separator;

        try {
            serverConfig = ServerConfigImpl.createFromPath(baseConfigPath, tempPath);
        } catch (IOException e) {
            LOGGER.warning("Unable to read server config. Using the default configuration. " + e.getMessage());
            serverConfig = new ServerConfigImpl(basePath, tempPath, new Properties());
        }

        serverConfig.setResourceProviders(selectActiveResourceProviders(serverConfig));
        return serverConfig;
    }

    private List<ServletContextImpl> getServletContexts(final ServerConfig serverConfig) {
        DeploymentDescriptorBuilder deploymentDescriptorBuilder
                = getDeploymentDescriptorBuilder(new FileSessionStorage(serverConfig.getTempPath()), serverConfig);

        List<ServletContextImpl> servletContexts = deploymentDescriptorBuilder.build();

        for (ServletContextImpl servletContextImpl : servletContexts) {
            for (Map.Entry<String, Object> entry : getAdditionalServletContextAttributes().entrySet()) {
                servletContextImpl.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        return servletContexts;
    }

    /**
     * For performance reasons ServletResourceProvider is the last resource provider.
     *
     * @param serverConfig
     * @return
     */
    private List<ResourceProvider> selectActiveResourceProviders(final ServerConfig serverConfig) {
        List<ResourceProvider> resourceProviders = new ArrayList<>();

        resourceProviders.add(getFileResourceProvider(serverConfig));

        for (ResourceProvider resourceProvider : getAdditionalResourceProviders(serverConfig)) {
            resourceProviders.add(resourceProvider);
        }

        resourceProviders.add(getServletResourceProvider(serverConfig));
        return resourceProviders;
    }

    private FileResourceProvider getFileResourceProvider(final ServerConfig serverConfig) {
        return new FileResourceProvider(new RangeParser(), new RangeHelper(),
                new RangePartHeaderSerializer(), serverConfig.getMimeTypeMapping(),
                serverConfig.getDocumentRootPath());
    }

    private ServletResourceProvider getServletResourceProvider(final ServerConfig serverConfig) {
        return new ServletResourceProvider(
                new ServletContainerImpl(),
                getServletContexts(serverConfig)
        );
    }
}
