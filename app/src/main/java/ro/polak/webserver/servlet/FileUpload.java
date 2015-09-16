/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.util.Vector;

/**
 * Handles file upload
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/22.02.2008
 */
public class FileUpload {

    private Vector<UploadedFile> uploadedFiles;
    private int popCounter = -1;

    /**
     * Default constructor
     *
     * @param uploadedFiles Vector of uploaded files
     */
    public FileUpload(Vector<UploadedFile> uploadedFiles)// <UploadedFile>
    {
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
    public UploadedFile getFile(String fileFormName) {
        if (uploadedFiles == null || uploadedFiles.size() == 0) {
            return null;
        }
        for (int i = 0; i < uploadedFiles.size(); i++) {
            if (((UploadedFile) uploadedFiles.elementAt(i)).getName().equals(fileFormName)) {
                return (UploadedFile) uploadedFiles.elementAt(i);
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
            return (UploadedFile) uploadedFiles.elementAt(popCounter);
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
            ((UploadedFile) uploadedFiles.elementAt(i)).destroy();
        }

        uploadedFiles = null;
        System.gc();
    }
}
