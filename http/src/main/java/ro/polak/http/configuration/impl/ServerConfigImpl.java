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
 * Server configuration.
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

    public ServerConfigImpl(String basePath, String tempPath, Properties properties) {
        this.tempPath = tempPath;
        this.basePath = basePath;
        this.properties = properties;

        assignListenPort(properties, 8080);
        assignDocumentRoot(basePath, properties, basePath + "www" + File.separator);
        assignMaxThreads(properties, 10);
        assignKeepAlive(properties, false);
        assign404Document(basePath, properties);
        assign403Document(basePath, properties);
        try {
            assignMimeMapping(basePath, properties, new MimeTypeMappingImpl());
        } catch (IOException ignored) {
            // Do nothing
        }
        assignDirectoryIndex(properties, Arrays.asList("index.html", "index.htm", "Index"));
    }

    /**
     * @param basePath
     * @param tempPath
     * @return
     * @throws IOException
     */
    public static ServerConfigImpl createFromPath(final String basePath, final String tempPath) throws IOException {
        Properties properties = loadProperties(basePath);

        ServerConfigImpl serverConfig = new ServerConfigImpl(basePath, tempPath, properties);
        serverConfig.basePath = basePath;
        return serverConfig;
    }

    private static Properties loadProperties(final String basePath) throws IOException {
        InputStream configInputStream = new FileInputStream(basePath + PROPERTIES_FILE_NAME);

        Properties properties = new Properties();
        try {
            properties.load(configInputStream);
        } finally {
            IOUtilities.closeSilently(configInputStream);
        }
        return properties;
    }

    private void assignDirectoryIndex(final Properties properties, final List<String> defaultValue) {
        if (getResolvedProperty(properties, ATTRIBUTE_DIRECTORY_INDEX) != null) {
            directoryIndex = new ArrayList<>();
            String directoryIndexLine[] = getResolvedProperty(properties, ATTRIBUTE_DIRECTORY_INDEX).split(",");
            for (int i = 0; i < directoryIndexLine.length; i++) {
                String index = directoryIndexLine[i].trim();
                if (!"".equals(index)) {
                    directoryIndex.add(directoryIndexLine[i]);
                }
            }
        } else {
            directoryIndex = defaultValue;
        }
    }

    private void assignMimeMapping(final String basePath, final Properties properties, final MimeTypeMapping defaultValue) throws IOException {
        if (getResolvedProperty(properties, ATTRIBUTE_MIME_TYPE) != null) {
            String defaultMimeType = "text/plain";
            if (getResolvedProperty(properties, ATTRIBUTE_DEFAULT_MIME_TYPE) != null) {
                defaultMimeType = getResolvedProperty(properties, ATTRIBUTE_DEFAULT_MIME_TYPE);
            }

            InputStream mimeInputStream = new FileInputStream(basePath + getResolvedProperty(properties, ATTRIBUTE_MIME_TYPE));
            try {
                mimeTypeMapping = MimeTypeMappingImpl.createFromStream(mimeInputStream, defaultMimeType);
            } finally {
                IOUtilities.closeSilently(mimeInputStream);
            }
        } else {
            mimeTypeMapping = defaultValue;
        }
    }

    private void assign403Document(final String basePath, final Properties properties) {
        if (getResolvedProperty(properties, ATTRIBUTE_ERROR_DOCUMENT_403) != null) {
            errorDocument403Path =
                    basePath + getResolvedProperty(properties, ATTRIBUTE_ERROR_DOCUMENT_403);
        }
    }

    private void assign404Document(String basePath, Properties properties) {
        if (getResolvedProperty(properties, ATTRIBUTE_ERROR_DOCUMENT_404) != null) {
            this.errorDocument404Path =
                    basePath + getResolvedProperty(properties, ATTRIBUTE_ERROR_DOCUMENT_404);
        }
    }

    private void assignKeepAlive(Properties properties, boolean defaultValue) {
        if (getResolvedProperty(properties, ATTRIBUTE_KEEP_ALIVE) != null) {
            keepAlive =
                    getResolvedProperty(properties, ATTRIBUTE_KEEP_ALIVE).equalsIgnoreCase(TRUE);
        } else {
            keepAlive = defaultValue;
        }
    }

    private void assignMaxThreads(Properties properties, int defaultValue) {
        if (getResolvedProperty(properties, ATTRIBUTE_MAX_THREADS) != null) {
            maxServerThreads =
                    Integer.parseInt(getResolvedProperty(properties, ATTRIBUTE_MAX_THREADS));
        } else {
            maxServerThreads = defaultValue;
        }
    }

    private void assignDocumentRoot(String basePath, Properties properties, String defaultValue) {
        if (getResolvedProperty(properties, ATTRIBUTE_STATIC_PATH) != null) {
            documentRootPath = basePath + getResolvedProperty(properties, ATTRIBUTE_STATIC_PATH);
        } else {
            documentRootPath = defaultValue;
        }
    }

    private void assignListenPort(Properties properties, int defaultValue) {
        if (getResolvedProperty(properties, ATTRIBUTE_PORT) != null) {
            listenPort = Integer.parseInt(getResolvedProperty(properties, ATTRIBUTE_PORT));
        } else {
            listenPort = defaultValue;
        }
    }

    /**
     * Returns specified property. System properties take precedence to the file defined properties.
     *
     * @param properties
     * @param propertyName
     * @return
     */
    private static String getResolvedProperty(Properties properties, String propertyName) {
        return System.getProperty(propertyName, properties.getProperty(propertyName));
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
        return getResolvedProperty(properties, name);
    }
}
