/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.util.List;

/**
 * Handles file upload
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class FileUpload {

    private List<UploadedFile> uploadedFiles;
    private int popCounter = -1;

    /**
     * Default constructor
     *
     * @param uploadedFiles ArrayList of uploaded files
     */
    public FileUpload(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    /**
     * Creates an empty file upload
     */
    public FileUpload() {
    }

    /**
     * Returns an uploaded file of specified form name
     *
     * @param fileFormName HTTP field name for the file
     * @return specified UploadedFile
     */
    public UploadedFile get(String fileFormName) {
        if (uploadedFiles == null || uploadedFiles.size() == 0) {
            return null;
        }
        for (int i = 0; i < uploadedFiles.size(); i++) {
            if (uploadedFiles.get(i).getPostFieldName().equals(fileFormName)) {
                return uploadedFiles.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the next file from the stack
     *
     * @return next UploadedFile
     */
    public UploadedFile pop() {
        if (++popCounter < uploadedFiles.size()) {
            return uploadedFiles.get(popCounter);
        }
        return null;
    }

    /**
     * Rewinds the file upload file stack
     */
    public void rewind() {
        popCounter = -1;
    }

    /**
     * Returns the number of uploaded files
     *
     * @return number of uploaded files
     */
    public int size() {
        if (uploadedFiles == null) {
            return 0;
        }
        return uploadedFiles.size();
    }

    /**
     * Destroys (deletes) all unused temporary data
     */
    public void freeResources() {
        if (uploadedFiles == null) {
            return;
        }

        for (int i = 0; i < uploadedFiles.size(); i++) {
            uploadedFiles.get(i).destroy();
        }

        uploadedFiles = null;
        System.gc();
    }
}
