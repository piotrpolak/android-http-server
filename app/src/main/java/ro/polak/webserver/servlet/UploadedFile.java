/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.File;

/**
 * Uploaded file representation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class UploadedFile {

    private String postFieldName;
    private String fileName;
    private File file;
    private boolean isMoved = false;
    private String initialPath;

    /**
     * Constructor
     *
     * @param postFieldName
     * @param fileName
     * @param file
     */
    public UploadedFile(String postFieldName, String fileName, File file) {
        this.postFieldName = postFieldName;
        this.fileName = fileName;
        this.file = file;
        this.initialPath = file.getAbsolutePath();
    }

    /**
     * Deletes temporary file if the file has not been moved to another location
     *
     * @return true if deleted
     */
    public boolean destroy() {
        if (!file.exists() || !initialPath.equals(file.getAbsolutePath())) {
            return false;
        }
        return file.delete();
    }

    /**
     * Returns the HTML form postFieldName
     *
     * @return the HTML form postFieldName
     */
    public String getPostFieldName() {
        return this.postFieldName;
    }

    /**
     * Returns the postFieldName of uploaded file
     *
     * @return the postFieldName of uploaded file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Returns uploaded file
     *
     * @return
     */
    public File getFile() {
        return file;
    }
}
