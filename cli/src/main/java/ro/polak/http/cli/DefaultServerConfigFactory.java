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
import java.util.regex.Pattern;

import example.Chunked;
import example.ChunkedWithDelay;
import example.Cookies;
import example.Forbidden;
import example.Index;
import example.InternalServerError;
import example.NotFound;
import example.Session;
import example.Streaming;
import ro.polak.http.ServerConfig;
import ro.polak.http.ServerConfigFactory;
import ro.polak.http.configuration.ServletContextBuilder;
import ro.polak.http.impl.ServerConfigImpl;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.resource.provider.impl.FileResourceProvider;
import ro.polak.http.resource.provider.impl.ServletResourceProvider;
import ro.polak.http.servlet.DefaultServletContainer;
import ro.polak.http.servlet.RangeHelper;
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
    protected Set<ResourceProvider> getAdditionalResourceProviders(ServerConfig serverConfig) {
        return new HashSet<>();
    }

    /**
     * Returns servlet context builder.
     *
     * @return
     */
    protected ServletContextBuilder getServletContextBuilder() {
        return ServletContextBuilder.create()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/Chunked.dhtml$"))
                    .withServletClass(Chunked.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/ChunkedWithDelay.dhtml$"))
                    .withServletClass(ChunkedWithDelay.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/Cookies.dhtml$"))
                    .withServletClass(Cookies.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/Forbidden.dhtml$"))
                    .withServletClass(Forbidden.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/Index.dhtml$"))
                    .withServletClass(Index.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/$"))
                    .withServletClass(Index.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/InternalServerError.dhtml$"))
                    .withServletClass(InternalServerError.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/NotFound.dhtml$"))
                    .withServletClass(NotFound.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/Session.dhtml$"))
                    .withServletClass(Session.class)
                .end()
                .addServlet()
                    .withUrlPattern(Pattern.compile("^example/Streaming.dhtml$"))
                    .withServletClass(Streaming.class)
                .end();

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
        ServletContextBuilder servletContextBuilder = getServletContextBuilder();
        servletContextBuilder.withServerConfig(serverConfig)
                .withSessionStorage(new FileSessionStorage(serverConfig.getTempPath()));

        for (Map.Entry<String, Object> entry : getAdditionalServletContextAttributes().entrySet()) {
            servletContextBuilder.withAttribute(entry.getKey(), entry.getValue());
        }

        return servletContextBuilder.build();
    }

    /**
     * For performance reasons ServletResourceProvider is the last resource provider.
     *
     * @param serverConfig
     * @return
     */
    private List<ResourceProvider> selectActiveResourceProviders(ServerConfig serverConfig) {
        List<ResourceProvider> resourceProviders = new ArrayList<>();

        resourceProviders.add(getFileResourceProvider(serverConfig));

        for (ResourceProvider resourceProvider : getAdditionalResourceProviders(serverConfig)) {
            resourceProviders.add(resourceProvider);
        }

        resourceProviders.add(getServletResourceProvider(serverConfig));
        return resourceProviders;
    }

    private FileResourceProvider getFileResourceProvider(ServerConfig serverConfig) {
        return new FileResourceProvider(new RangeParser(), new RangeHelper(),
                new RangePartHeaderSerializer(), serverConfig.getMimeTypeMapping(),
                serverConfig.getDocumentRootPath());
    }

    private ServletResourceProvider getServletResourceProvider(ServerConfig serverConfig) {
        return new ServletResourceProvider(
                new DefaultServletContainer(),
                getServletContext(serverConfig)
        );
    }
}
