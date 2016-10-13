/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

/**
 * Multipart request headers (for each multipart)
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartHeadersPart extends Headers {

    private String fileName;
    private String contentType;
    private String postFieldName;

    /**
     * Parses multipart headers
     *
     * @param headersString headers
     */
    public void parse(String headersString) {

        // Parsing header pairs
        super.parse(headersString, false);

        // Reading uploaded file name
        String contentDisposition = getHeader(Headers.HEADER_CONTENT_DISPOSITION);
        String name = contentDisposition.substring(contentDisposition.indexOf("name=\"") + 6);

        int quotationMarkPosition = name.indexOf("\"");
        if (quotationMarkPosition > -1) {
            name = name.substring(0, quotationMarkPosition);
        }

        // Getting file type
        String contentType = getHeader(Headers.HEADER_CONTENT_TYPE);

        // Getting file name
        String fileName = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10);
        fileName = fileName.substring(0, fileName.indexOf("\""));

        // Assigning values
        setPostFieldName(name);
        setContentType(contentType);
        setFileName(fileName);
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
