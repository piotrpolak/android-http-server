/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

import java.io.File;

/**
 * Uploaded file representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public final class UploadedFile {

    private final String postFieldName;
    private final String fileName;
    private final File file;

    /**
     * Constructor.
     *
     * @param postFieldName
     * @param fileName
     * @param file
     */
    public UploadedFile(final String postFieldName, final String fileName, final File file) {
        this.postFieldName = postFieldName;
        this.fileName = fileName;
        this.file = file;
    }

    /**
     * Deletes temporary file if the file has not been moved to another location.
     *
     * @return true if deleted
     */
    public boolean destroy() {
        if (file.exists()) {
            return file.delete();
        }

        return false;
    }

    /**
     * Returns the HTML form postFieldName.
     *
     * @return the HTML form postFieldName
     */
    public String getPostFieldName() {
        return postFieldName;
    }

    /**
     * Returns the postFieldName of uploaded file.
     *
     * @return the postFieldName of uploaded file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns uploaded file.
     *
     * @return
     */
    public File getFile() {
        return file;
    }
}
