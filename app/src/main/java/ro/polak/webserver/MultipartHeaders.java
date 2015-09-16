/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

/**
 * Multipart request headers (for each multipart)
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartHeaders extends Headers {

    private String fileName = null;
    private String contentType = null;
    private String postFieldName = null;

    /**
     * Parses multipart headers
     *
     * @param headersString headers
     */
    public void parse(String headersString) {

        // Parsing header pairs
        super.parse(headersString);

        // Reading uploaded file name
        String contentDisposition = this.getHeader("Content-Disposition");
        String n = contentDisposition.substring(contentDisposition.indexOf("name=\"") + 6);
        try {
            n = n.substring(0, n.indexOf("\""));
        } catch (Exception e) {
            // Do nothing
            //e.printStackTrace();
        }

        // Getting file type
        String ct = this.getHeader("Content-Type");

        // Getting file name
        String fn = null;
        if (ct != null) {
            fn = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10);
            fn = fn.substring(0, fn.indexOf("\""));
        }

        // Assigning values
        setPostFieldName(n);
        setContentType(ct);
        setFileName(fn);
    }

    /**
     * Returns the uploaded file name
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the content type of the uploaded file
     *
     * @return
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Returns the name of the form post field
     *
     * @return
     */
    public String getPostFieldName() {
        return postFieldName;
    }

    /**
     * Sets the uploaded file name
     *
     * @param fileName
     */
    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the content type of the uploaded file
     *
     * @param contentType
     */
    private void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the name of the form post field
     *
     * @param name
     */
    private void setPostFieldName(String name) {
        this.postFieldName = name;
    }
}
