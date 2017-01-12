/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.utilities;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.util.TimeZone.getTimeZone;

/**
 * Utilities
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200804
 */
public class Utilities {

    // TODO This class should be split into dedicated ones

    private static final SimpleDateFormat simpleDateFormat;
    public static final String CHARSET_NAME = "UTF-8";

    static {
        simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
        simpleDateFormat.setTimeZone(getTimeZone("GMT"));
    }

    /**
     * Returns the extension sting for a given file path
     *
     * @param filename filepath or filename
     * @return the extension part for a given file path
     */
    public static String getExtension(String filename) {

        if (filename == null) {
            return null;
        }

        filename = filename.toLowerCase();
        String ext = "";
        int pos;

        if ((pos = filename.lastIndexOf('/')) != -1) {
            filename = filename.substring(pos + 1);
        }

        if ((pos = filename.lastIndexOf('.')) != -1) {
            ext = filename.substring(pos + 1);
        }

        return ext;
    }

    /**
     * Once called, deletes all the files inside the temporary files directory
     */
    public static void clearDirectory(String directoryPath) {
        File f = new File(directoryPath);
        File files[] = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    /**
     * Encodes given string for URL/HTTP
     *
     * @param text text to be encoded
     * @return encoded string
     */
    public static String urlEncode(String text) {
        try {
            return java.net.URLEncoder.encode(text, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Decodes given string for URL/HTTP
     *
     * @param text text to be decoded
     * @return decoded string
     */
    public static String urlDecode(String text) {
        try {
            return java.net.URLDecoder.decode(text, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Returns user friendly representation of file size
     *
     * @param length size of a file
     * @return formated size of the file using B, KB, MB, GB
     */
    public static String fileSizeUnits(long length) {
        if (length < 1024) {
            return length + " B";
        }

        double size = (double) length;
        DecimalFormat format = new DecimalFormat("####0.00");

        if (length < 1048576) {
            return format.format(size / 1024) + " KB";
        } else if (length < 1073741824) {
            return format.format(size / 1048576) + " MB";
        } else {
            return format.format(size / 1073741824) + " GB";
        }
    }

    /**
     * Formats date into the RFC 822 GMT format.
     *
     * @param date
     * @return
     */
    public static String dateFormat(Date date) {
        return simpleDateFormat.format(date);
    }
}
