/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http;

/**
 * Multipart request headers (for each multipart).
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartHeadersPart {

    private String fileName;
    private String contentType;
    private String name;

    /**
     * Returns the uploaded file name.
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the content type of the uploaded file.
     *
     * @return
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the name of the form post field.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the uploaded file name.
     *
     * @param fileName
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the content type of the uploaded file.
     *
     * @param contentType
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the name of the form post field.
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }
}
