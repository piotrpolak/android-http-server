/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ro.polak.http.MimeTypeMapping;
import ro.polak.http.ServerConfig;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.utilities.ConfigReader;
import ro.polak.http.utilities.IOUtilities;

/**
 * Server configuration
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServerConfigImpl implements ServerConfig {

    private static final List<String> SUPPORTED_METHODS = Arrays.asList(
            HttpRequestWrapper.METHOD_GET,
            HttpRequestWrapper.METHOD_POST,
            HttpRequestWrapper.METHOD_HEAD
    );

    private static final String ATTRIBUTE_LISTEN = "Listen";
    private static final String ATTRIBUTE_DOCUMENT_ROOT = "DocumentRoot";
    private static final String ATTRIBUTE_MAX_THREADS = "MaxThreads";
    private static final String ATTRIBUTE_KEEP_ALIVE = "KeepAlive";
    private static final String ATTRIBUTE_ERROR_DOCUMENT_404 = "ErrorDocument404";
    private static final String ATTRIBUTE_ERROR_DOCUMENT_403 = "ErrorDocument403";
    private static final String ATTRIBUTE_SERVLET_MAPPED_EXTENSION = "ServletMappedExtension";
    private static final String ATTRIBUTE_DEFAULT_MIME_TYPE = "DefaultMimeType";
    private static final String ATTRIBUTE_MIME_TYPE = "MimeType";
    private static final String ATTRIBUTE_DIRECTORY_INDEX = "DirectoryIndex";

    private List<String> directoryIndex;
    private String basePath;
    private String documentRootPath;
    private String tempPath;
    private int listenPort;
    private String servletMappedExtension;
    private MimeTypeMapping mimeTypeMapping;
    private int maxServerThreads;
    private boolean keepAlive;
    private String errorDocument404Path;
    private String errorDocument403Path;
    private List<ResourceProvider> resourceProviders = Collections.emptyList();

    public ServerConfigImpl() {
        this("/httpd/temp/");
    }

    public ServerConfigImpl(String tempPath) {
        this.tempPath = tempPath;
        basePath = "/httpd/";
        documentRootPath = basePath + "www/";
        listenPort = 8080;
        servletMappedExtension = "dhtml";
        maxServerThreads = 10;
        directoryIndex = new ArrayList<>(Arrays.asList("Index.dhtml", "index.html", "index.htm"));
    }

    /**
     * @param basePath
     * @param tempPath
     * @return
     * @throws IOException
     */
    public static ServerConfigImpl createFromPath(String basePath, String tempPath) throws IOException {
        ConfigReader reader = new ConfigReader();
        InputStream configInputStream = new FileInputStream(basePath + "httpd.conf");

        Map<String, String> config;
        try {
            config = reader.read(configInputStream);
        } finally {
            IOUtilities.closeSilently(configInputStream);
        }

        ServerConfigImpl serverConfig = new ServerConfigImpl();
        serverConfig.basePath = basePath;
        serverConfig.tempPath = tempPath;
        serverConfig.documentRootPath = basePath + "www/";

        if (config.containsKey(ATTRIBUTE_LISTEN)) {
            serverConfig.listenPort = Integer.parseInt(config.get(ATTRIBUTE_LISTEN));
        }

        if (config.containsKey(ATTRIBUTE_DOCUMENT_ROOT)) {
            serverConfig.documentRootPath = basePath + config.get(ATTRIBUTE_DOCUMENT_ROOT);
        }

        if (config.containsKey(ATTRIBUTE_MAX_THREADS)) {
            serverConfig.maxServerThreads = Integer.parseInt(config.get(ATTRIBUTE_MAX_THREADS));
        }

        if (config.containsKey(ATTRIBUTE_KEEP_ALIVE)) {
            serverConfig.keepAlive = config.get(ATTRIBUTE_KEEP_ALIVE).equalsIgnoreCase("on");
        }

        if (config.containsKey(ATTRIBUTE_ERROR_DOCUMENT_404)) {
            serverConfig.errorDocument404Path = basePath + config.get(ATTRIBUTE_ERROR_DOCUMENT_404);
        }
        if (config.containsKey(ATTRIBUTE_ERROR_DOCUMENT_403)) {
            serverConfig.errorDocument403Path = basePath + config.get(ATTRIBUTE_ERROR_DOCUMENT_403);
        }

        if (config.containsKey(ATTRIBUTE_SERVLET_MAPPED_EXTENSION)) {
            serverConfig.servletMappedExtension = config.get(ATTRIBUTE_SERVLET_MAPPED_EXTENSION);
        }

        // Initializing mime mapping
        if (config.containsKey(ATTRIBUTE_MIME_TYPE)) {
            String defaultMimeType = "text/plain";
            if (config.containsKey(ATTRIBUTE_DEFAULT_MIME_TYPE)) {
                defaultMimeType = config.get(ATTRIBUTE_DEFAULT_MIME_TYPE);
            }

            InputStream mimeInputStream = new FileInputStream(basePath + config.get(ATTRIBUTE_MIME_TYPE));
            try {
                serverConfig.mimeTypeMapping = MimeTypeMappingImpl.createFromStream(mimeInputStream, defaultMimeType);
            } finally {
                IOUtilities.closeSilently(mimeInputStream);
            }
        }

        // Generating index files
        if (config.containsKey(ATTRIBUTE_DIRECTORY_INDEX)) {
            String directoryIndexLine[] = config.get(ATTRIBUTE_DIRECTORY_INDEX).split(" ");
            for (int i = 0; i < directoryIndexLine.length; i++) {
                serverConfig.directoryIndex.add(directoryIndexLine[i]);
            }
        }

        if (serverConfig.mimeTypeMapping == null) {
            // Initializing an empty mime type mapping to prevent null pointer exceptions
            serverConfig.mimeTypeMapping = new MimeTypeMappingImpl();
        }

        return serverConfig;
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
    public String getServletMappedExtension() {
        return servletMappedExtension;
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
}
