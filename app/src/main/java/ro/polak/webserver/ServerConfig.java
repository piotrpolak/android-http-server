/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ro.polak.utilities.Config;
import ro.polak.webserver.controller.MainController;

/**
 * Server configuration
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServerConfig extends Config {

    private String basePath;
    private String documentRootPath;
    private String tempPath;
    private int listenPort;
    private String servletMappedExtension;
    private MimeTypeMapping mimeTypeMapping;
    private String defaultMimeType;
    private int maxServerThreads;
    private boolean keepAlive;
    private String errorDocument404Path;
    private String errorDocument403Path;
    private long servletServicePoolPingerInterval;
    private long servletServicePoolServletExpires;
    public ArrayList directoryIndex = new ArrayList();

    public ServerConfig(String basePath, String tempPath) {
        this.basePath = basePath;
        documentRootPath = basePath + "www/";
        this.tempPath = tempPath;
        listenPort = 8080;
        servletMappedExtension = "dhtml";
        defaultMimeType = "text/plain";
        maxServerThreads = 50;
        servletServicePoolPingerInterval = 10000;
        servletServicePoolServletExpires = 30000;

        this.read();
    }

    /**
     * Reads the config from the file
     */
    public void read() {
        if (super.read(basePath + "httpd.conf")) {
            // Assigning values
            listenPort = Integer.parseInt(this.get("Listen"));
            documentRootPath = basePath + this.get("DocumentRoot");
            defaultMimeType = this.get("DefaultMimeType");
            maxServerThreads = Integer.parseInt(this.get("MaxThreads"));
            keepAlive = this.get("KeepAlive").toLowerCase().equals("on");

            if (this.get("ErrorDocument404") != null) {
                errorDocument404Path = basePath + this.get("ErrorDocument404");
            }
            if (this.get("ErrorDocument403") != null) {
                errorDocument403Path = basePath + this.get("ErrorDocument403");
            }

            servletMappedExtension = this.get("ServletMappedExtension");

            // Initializing mime mapping
            try {
                mimeTypeMapping = new MimeTypeMapping(new FileInputStream(basePath + this.get("MimeType")), this.get("DefaultMimeType"));
                MainController.getInstance().println(this.getClass(), "Read mime type config: " + basePath + this.get("MimeType"));
            } catch (IOException e) {
                MainController.getInstance().println(this.getClass(), "Unable to read mime type config: " + basePath + this.get("MimeType"));
            }


            // Generating index files
            String directoryIndexLine[] = this.get("DirectoryIndex").split(" ");
            for (int i = 0; i < directoryIndexLine.length; i++) {
                directoryIndex.add(directoryIndexLine[i]);
            }
        }

        if (mimeTypeMapping == null) {
            // Initializing an empty mime type mapping to prevent null pointer exceptions
            mimeTypeMapping = new MimeTypeMapping();
        }

    }

    /**
     * Returns base path
     *
     * @return
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Returns document root path
     *
     * @return
     */
    public String getDocumentRootPath() {
        return documentRootPath;
    }

    /**
     * Returns server temp path
     *
     * @return
     */
    public String getTempPath() {
        return tempPath;
    }

    /**
     * Returns the listen port
     *
     * @return
     */
    public int getListenPort() {
        return listenPort;
    }

    /**
     * Returns the servlet mapped extension
     *
     * @return
     */
    public String getServletMappedExtension() {
        return servletMappedExtension;
    }

    /**
     * Returns the mime type mapping
     *
     * @return
     */
    public MimeTypeMapping getMimeTypeMapping() {
        return mimeTypeMapping;
    }

    /**
     * Returns the default mime type
     *
     * @return
     */
    public String getDefaultMimeType() {
        return defaultMimeType;
    }

    /**
     * Returns the number of maximum allowed threads
     *
     * @return
     */
    public int getMaxServerThreads() {
        return maxServerThreads;
    }

    /**
     * Returns whether the server should keep the connections alive
     *
     * @return
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * Returns error 404 file path
     *
     * @return
     */
    public String getErrorDocument404Path() {
        return errorDocument404Path;
    }

    /**
     * Returns the error 403 file path
     *
     * @return
     */
    public String getErrorDocument403Path() {
        return errorDocument403Path;
    }

    /**
     * Returns the servlet service pinger interval
     *
     * @return
     */
    public long getServletServicePoolPingerInterval() {
        return servletServicePoolPingerInterval;
    }

    /**
     * Returns the servlet service expires time
     *
     * @return
     */
    public long getServletServicePoolServletExpires() {
        return servletServicePoolServletExpires;
    }

    /**
     * Returns the directory index
     *
     * @return
     */
    public ArrayList<String> getDirectoryIndex() {
        return directoryIndex;
    }

}
