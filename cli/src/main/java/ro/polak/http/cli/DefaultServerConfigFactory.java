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

import example.ChunkedServlet;
import example.ChunkedWithDelayServlet;
import example.CookiesServlet;
import example.ForbiddenServlet;
import example.ForbiddenByFilterServlet;
import example.IndexServlet;
import example.InternalServerErrorServlet;
import example.NotFoundServlet;
import example.SessionServlet;
import example.StreamingServlet;
import example.filter.FakeSecuredAbstractFilter;
import ro.polak.http.DefaultServlet;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.ServerConfigFactory;
import ro.polak.http.configuration.DeploymentDescriptorBuilder;
import ro.polak.http.configuration.impl.ServerConfigImpl;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.resource.provider.FileSystemResourceProvider;
import ro.polak.http.ServletDispatcher;
import ro.polak.http.servlet.impl.ServletContainerImpl;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.servlet.impl.ServletContextImpl;
import ro.polak.http.session.storage.FileSessionStorage;
import ro.polak.http.session.storage.SessionStorage;
import ro.polak.http.utilities.DateProvider;

/**
 * Default server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class DefaultServerConfigFactory implements ServerConfigFactory {

    private static final Logger LOGGER = Logger.getLogger(DefaultServerConfigFactory.class.getName());
    private static final long SERVLET_TIME_TO_LIVE_IN_MS = 1800L * 1000L;
    private static final long MONITORING_INTERVAL_IN_MS = SERVLET_TIME_TO_LIVE_IN_MS / 10L;

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
                        .withUrlExcludedPattern(Pattern.compile("^/secured/Logout$"))
                        .withFilterClass(FakeSecuredAbstractFilter.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Chunked$"))
                        .withServletClass(ChunkedServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/ChunkedWithDelay$"))
                        .withServletClass(ChunkedWithDelayServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Cookies$"))
                        .withServletClass(CookiesServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Forbidden$"))
                        .withServletClass(ForbiddenServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Index$"))
                        .withServletClass(IndexServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/$"))
                        .withServletClass(IndexServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/InternalServerError$"))
                        .withServletClass(InternalServerErrorServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/NotFound$"))
                        .withServletClass(NotFoundServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Session$"))
                        .withServletClass(SessionServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/Streaming$"))
                        .withServletClass(StreamingServlet.class)
                    .end()
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^/secured/ForbiddenByFilter"))
                        .withServletClass(ForbiddenByFilterServlet.class)
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
        serverConfig.setServletDispatcher(getServletDispatcher(serverConfig));

        return serverConfig;
    }

    private List<ServletContextImpl> getServletContexts(final ServerConfig serverConfig) {
        DeploymentDescriptorBuilder deploymentDescriptorBuilder
                = getDeploymentDescriptorBuilder(new FileSessionStorage(serverConfig.getTempPath()), serverConfig);

        appendDefaultServlet(deploymentDescriptorBuilder, serverConfig);

        List<ServletContextImpl> servletContexts = deploymentDescriptorBuilder.build();

        for (ServletContextImpl servletContextImpl : servletContexts) {
            for (Map.Entry<String, Object> entry : getAdditionalServletContextAttributes().entrySet()) {
                servletContextImpl.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        return servletContexts;
    }

    private void appendDefaultServlet(DeploymentDescriptorBuilder deploymentDescriptorBuilder, ServerConfig serverConfig) {
        deploymentDescriptorBuilder
                .addServletContext()
                    .withContextPath("/")
                    .withAttribute("ResourceProviders", serverConfig.getResourceProviders())
                    .withAttribute("DirectoryIndex", serverConfig.getDirectoryIndex())
                    .addServlet()
                        .withUrlPattern(Pattern.compile("^.*$"))
                        .withServletClass(DefaultServlet.class)
                    .end()
                .end();
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

        return resourceProviders;
    }

    private FileSystemResourceProvider getFileResourceProvider(final ServerConfig serverConfig) {
        return new FileSystemResourceProvider(new RangeParser(), new RangeHelper(),
                new RangePartHeaderSerializer(), serverConfig.getMimeTypeMapping(),
                serverConfig.getDocumentRootPath());
    }

    private ServletDispatcher getServletDispatcher(final ServerConfig serverConfig) {
        return new ServletDispatcher(
                new ServletContainerImpl(new DateProvider(), SERVLET_TIME_TO_LIVE_IN_MS, MONITORING_INTERVAL_IN_MS),
                getServletContexts(serverConfig)
        );
    }
}
