/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.configuration.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import ro.polak.http.MimeTypeMapping;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.impl.MimeTypeMappingImpl;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.impl.HttpRequestImpl;
import ro.polak.http.utilities.IOUtilities;

/**
 * Server configuration
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServerConfigImpl implements ServerConfig {

    public static final String TRUE = "true";
    public static final String PROPERTIES_FILE_NAME = "httpd.properties";


    private static final List<String> SUPPORTED_METHODS = Arrays.asList(
            HttpRequestImpl.METHOD_GET,
            HttpRequestImpl.METHOD_POST,
            HttpRequestImpl.METHOD_HEAD
    );

    private static final String ATTRIBUTE_PORT = "server.port";
    private static final String ATTRIBUTE_STATIC_PATH = "server.static.path";
    private static final String ATTRIBUTE_MAX_THREADS = "server.maxThreads";
    private static final String ATTRIBUTE_KEEP_ALIVE = "server.keepAlive.enabled";
    private static final String ATTRIBUTE_ERROR_DOCUMENT_404 = "server.errorDocument.404";
    private static final String ATTRIBUTE_ERROR_DOCUMENT_403 = "server.errorDocument.403";
    private static final String ATTRIBUTE_DEFAULT_MIME_TYPE = "server.mimeType.defaultMimeType";
    private static final String ATTRIBUTE_MIME_TYPE = "server.mimeType.filePath";
    private static final String ATTRIBUTE_DIRECTORY_INDEX = "server.static.directoryIndex";

    private List<String> directoryIndex;
    private String basePath;
    private String documentRootPath;
    private String tempPath;
    private int listenPort;
    private MimeTypeMapping mimeTypeMapping;
    private int maxServerThreads;
    private boolean keepAlive;
    private String errorDocument404Path;
    private String errorDocument403Path;
    private List<ResourceProvider> resourceProviders = Collections.emptyList();
    private Properties properties = new Properties();

    public ServerConfigImpl() {
        this(File.separator + "httpd" + File.separator + "temp" + File.separator);
    }

    public ServerConfigImpl(String tempPath) {
        this.tempPath = tempPath;
        basePath = File.separator + "httpd" + File.separator;
        documentRootPath = basePath + "www" + File.separator;
        listenPort = 8080;
        maxServerThreads = 10;
        directoryIndex = new ArrayList<>(Arrays.asList("index.html", "index.htm", "Index"));

    }

    /**
     * @param basePath
     * @param tempPath
     * @return
     * @throws IOException
     */
    public static ServerConfigImpl createFromPath(String basePath, String tempPath) throws IOException {
        Properties properties = loadProperties(basePath);

        ServerConfigImpl serverConfig = new ServerConfigImpl();
        serverConfig.basePath = basePath;
        serverConfig.tempPath = tempPath;
        serverConfig.documentRootPath = basePath + "www" + File.separator;
        serverConfig.properties = properties;

        assignListenPort(properties, serverConfig);
        assignDocumentRoot(basePath, properties, serverConfig);
        assignMaxThreads(properties, serverConfig);
        assignKeepAlive(properties, serverConfig);
        assign404Document(basePath, properties, serverConfig);
        assign403Document(basePath, properties, serverConfig);
        assignMimeMapping(basePath, properties, serverConfig);
        assignDirectoryIndex(properties, serverConfig);

        return serverConfig;
    }

    private static Properties loadProperties(String basePath) throws IOException {
        InputStream configInputStream = new FileInputStream(basePath + PROPERTIES_FILE_NAME);

        Properties properties = new Properties();
        properties.load(configInputStream);
        try {
            properties.load(configInputStream);
        } finally {
            IOUtilities.closeSilently(configInputStream);
        }
        return properties;
    }

    private static void assignDirectoryIndex(Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_DIRECTORY_INDEX)) {
            serverConfig.directoryIndex.clear();
            String directoryIndexLine[] = properties.getProperty(ATTRIBUTE_DIRECTORY_INDEX).split(",");
            for (int i = 0; i < directoryIndexLine.length; i++) {
                String index = directoryIndexLine[i].trim();
                if (!"" .equals(index)) {
                    serverConfig.directoryIndex.add(directoryIndexLine[i]);
                }
            }
        }
    }

    private static void assignMimeMapping(String basePath, Properties properties, ServerConfigImpl serverConfig) throws IOException {
        if (properties.containsKey(ATTRIBUTE_MIME_TYPE)) {
            String defaultMimeType = "text/plain";
            if (properties.containsKey(ATTRIBUTE_DEFAULT_MIME_TYPE)) {
                defaultMimeType = properties.getProperty(ATTRIBUTE_DEFAULT_MIME_TYPE);
            }

            InputStream mimeInputStream = new FileInputStream(basePath + properties.getProperty(ATTRIBUTE_MIME_TYPE));
            try {
                serverConfig.mimeTypeMapping = MimeTypeMappingImpl.createFromStream(mimeInputStream, defaultMimeType);
            } finally {
                IOUtilities.closeSilently(mimeInputStream);
            }
        }

        if (serverConfig.mimeTypeMapping == null) {
            serverConfig.mimeTypeMapping = new MimeTypeMappingImpl();
        }
    }

    private static void assign403Document(String basePath, Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_ERROR_DOCUMENT_403)) {
            serverConfig.errorDocument403Path =
                    basePath + properties.getProperty(ATTRIBUTE_ERROR_DOCUMENT_403);
        }
    }

    private static void assign404Document(String basePath, Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_ERROR_DOCUMENT_404)) {
            serverConfig.errorDocument404Path =
                    basePath + properties.getProperty(ATTRIBUTE_ERROR_DOCUMENT_404);
        }
    }

    private static void assignKeepAlive(Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_KEEP_ALIVE)) {
            serverConfig.keepAlive =
                    properties.getProperty(ATTRIBUTE_KEEP_ALIVE).equalsIgnoreCase(TRUE);
        }
    }

    private static void assignMaxThreads(Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_MAX_THREADS)) {
            serverConfig.maxServerThreads =
                    Integer.parseInt(properties.getProperty(ATTRIBUTE_MAX_THREADS));
        }
    }

    private static void assignDocumentRoot(String basePath, Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_STATIC_PATH)) {
            serverConfig.documentRootPath = basePath + properties.getProperty(ATTRIBUTE_STATIC_PATH);
        }
    }

    private static void assignListenPort(Properties properties, ServerConfigImpl serverConfig) {
        if (properties.containsKey(ATTRIBUTE_PORT)) {
            serverConfig.listenPort = Integer.parseInt(properties.getProperty(ATTRIBUTE_PORT));
        }
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public String getDocumentRootPath() {
        return documentRootPath;
    }

    @Override
    public String getTempPath() {
        return tempPath;
    }

    @Override
    public int getListenPort() {
        return listenPort;
    }

    @Override
    public MimeTypeMapping getMimeTypeMapping() {
        return mimeTypeMapping;
    }

    @Override
    public int getMaxServerThreads() {
        return maxServerThreads;
    }

    @Override
    public boolean isKeepAlive() {
        return keepAlive;
    }

    @Override
    public String getErrorDocument404Path() {
        return errorDocument404Path;
    }

    @Override
    public String getErrorDocument403Path() {
        return errorDocument403Path;
    }

    @Override
    public List<String> getDirectoryIndex() {
        return directoryIndex;
    }

    @Override
    public List<String> getSupportedMethods() {
        return SUPPORTED_METHODS;
    }

    @Override
    public List<ResourceProvider> getResourceProviders() {
        return resourceProviders;
    }

    public void setResourceProviders(List<ResourceProvider> resourceProviders) {
        this.resourceProviders = resourceProviders;
    }

    @Override
    public String getAttribute(String name) {
        return properties.getProperty(name);
    }
}
