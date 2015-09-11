package ro.polak.webserver;

import java.io.File;
import java.util.Vector;

import android.os.Environment;
import android.util.Log;

import ro.polak.utilities.Config;

public class JLWSConfig {

    private static String basePath = Environment.getExternalStorageDirectory() + "/httpd/";
    public static int Listen = 8080;
    public static String DocumentRoot = JLWSConfig.basePath + "www/";
    public static String TempDir = JLWSConfig.basePath + "temp/";
    public static String ServletMappedExtension = "dhtml";
    public static Vector<String> DirectoryIndex = new Vector<String>(5);
    public static MimeType MimeType = null;
    public static String DefaultMimeType = "text/plain";
    public static int MaxThreads = 50;
    public static boolean KeepAlive = false;
    public static String ErrorDocument404 = null;
    public static String ErrorDocument403 = null;
    public static long ServletServicePoolPingerInterval = 10000;
    public static long ServletServicePoolServletExpires = 30000;

    public static String getBaseFilesPath() {
        return basePath;
    }

    /**
     * Initializes the configuration
     *
     * @return
     */
    public static boolean initialize() {
        Config config = new Config();

        if (config.read(JLWSConfig.basePath + "httpd.conf")) {
            Log.i("HTTP", "Config file read");
            JLWSConfig.Listen = Integer.parseInt(config.get("Listen"));
            JLWSConfig.DocumentRoot = basePath + config.get("DocumentRoot");
            JLWSConfig.TempDir = basePath + config.get("TempDir");

            try {
                File tmp_dir = new File(JLWSConfig.TempDir);
                if (!tmp_dir.exists()) {
                    tmp_dir.mkdir();
                }
            } catch (Exception e) {
            }

            JLWSConfig.ServletMappedExtension = config
                    .get("ServletMappedExtension");

            // TODO Mimetype
            JLWSConfig.MimeType = new MimeType(JLWSConfig.basePath + config.get("MimeType"), config.get("DefaultMimeType"));
            JLWSConfig.DefaultMimeType = config.get("DefaultMimeType");
            JLWSConfig.MaxThreads = Integer.parseInt(config.get("MaxThreads"));
            JLWSConfig.KeepAlive = config.get("KeepAlive").equals("On");
            JLWSConfig.ErrorDocument404 = JLWSConfig.basePath + config.get("ErrorDocument404");
            JLWSConfig.ErrorDocument403 = JLWSConfig.basePath + config.get("ErrorDocument403");

            String directoryIntex[] = config.get("DirectoryIndex").split(" ");
            for (int i = 0; i < directoryIntex.length; i++) {
                JLWSConfig.DirectoryIndex.addElement(directoryIntex[i]);
            }

            return true;
        }

        return false;
    }
}
