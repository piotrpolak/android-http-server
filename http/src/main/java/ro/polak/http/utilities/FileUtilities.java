/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.utilities;

import java.io.File;
import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * FileUtilities.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200804
 */
public final class FileUtilities {

    private static final Logger LOGGER = Logger.getLogger(FileUtilities.class.getName());
    private static final int BYTES_IN_KILOBYTE = 1024;
    private static final int BYTES_IN_MEGABYTE = 1048576;
    private static final int BYTES_IN_GIGABYTE = 1073741824;

    private FileUtilities() {
    }

    /**
     * Returns the extension sting for a given file path.
     *
     * @param filename filepath or filename
     * @return the extension part for a given file path
     */
    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }

        String filenameNormalized = filename.toLowerCase();
        String ext = "";
        int pos;

        if ((pos = filenameNormalized.lastIndexOf('/')) != -1) {
            filenameNormalized = filenameNormalized.substring(pos + 1);
        }

        if ((pos = filenameNormalized.lastIndexOf('.')) != -1) {
            ext = filenameNormalized.substring(pos + 1);
        }

        return ext;
    }

    /**
     * Once called, deletes all the files inside the temporary files directory.
     */
    public static void clearDirectory(final String directoryPath) {
        File f = new File(directoryPath);
        File[] files = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].delete()) {
                    LOGGER.severe("Unable to delete " + files[i].getAbsolutePath());
                }
            }
        }
    }

    /**
     * Returns user friendly representation of file size.
     *
     * @param length size of a file
     * @return formatted size of the file using B, KB, MB, GB
     */
    public static String fileSizeUnits(final long length) {
        if (length < BYTES_IN_KILOBYTE) {
            return length + " B";
        }

        double size = (double) length;
        DecimalFormat format = new DecimalFormat("####0.00");

        if (length < BYTES_IN_MEGABYTE) {
            return format.format(size / BYTES_IN_KILOBYTE) + " KB";
        } else if (length < BYTES_IN_GIGABYTE) {
            return format.format(size / BYTES_IN_MEGABYTE) + " MB";
        } else {
            return format.format(size / BYTES_IN_GIGABYTE) + " GB";
        }
    }
}
