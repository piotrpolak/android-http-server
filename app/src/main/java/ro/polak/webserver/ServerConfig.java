package ro.polak.webserver;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.util.Vector;
import ro.polak.utilities.Config;

/**
 * Server configuration
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 201509
 */
public class ServerConfig extends Config{

    private String basePath = Environment.getExternalStorageDirectory() + "/httpd/";
    private String documentRootPath = basePath + "www/";
    private String tempPath = basePath + "temp/";
    private int listenPort = 8080;
    private String servletMappedExtension = "dhtml";
    private MimeTypeMapping mimeTypeMapping = null;
    private String defaultMimeType = "text/plain";
    private int maxServerThreads = 50;
    private boolean keepAlive = false;
    private String errorDocument404Path = null;
    private String errorDocument403Path = null;
    private long servletServicePoolPingerInterval = 10000;
    private long servletServicePoolServletExpires = 30000;
    public static Vector<String> directoryIndex = new Vector<String>(5);

    /**
     * Default constructor
     */
    public ServerConfig()
    {
        this.read();
    }

    /**
     * Reads the config from the file
     */
    public void read()
    {
        if( super.read(basePath + "httpd.conf") )
        {
            Log.i("HTTP", "Config file read");

            // Assigning values
            listenPort = Integer.parseInt(this.get("Listen"));
            documentRootPath = basePath + this.get("DocumentRoot");
            tempPath = basePath + this.get("TempDir");
            defaultMimeType = this.get("DefaultMimeType");
            maxServerThreads = Integer.parseInt(this.get("MaxThreads"));
            keepAlive = this.get("KeepAlive").toLowerCase().equals("on");
            errorDocument404Path = basePath + this.get("ErrorDocument404");
            errorDocument403Path = basePath + this.get("ErrorDocument403");
            servletMappedExtension = this.get("ServletMappedExtension");

            // Creating temp directory
            try {
                File tmp_dir = new File(tempPath);
                if (!tmp_dir.exists()) {
                    tmp_dir.mkdir();
                }
            } catch (Exception e) {
            }

            // Initializing mime mapping
            mimeTypeMapping = new MimeTypeMapping(basePath + this.get("MimeTypeMapping"), this.get("DefaultMimeType"));

            // Generating index files
            String directoryIndexLine[] = this.get("DirectoryIndex").split(" ");
            for (int i = 0; i < directoryIndexLine.length; i++) {
                directoryIndex.addElement(directoryIndexLine[i]);
            }
        }
        else
        {
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
    public static Vector<String> getDirectoryIndex() {
        return directoryIndex;
    }

}
