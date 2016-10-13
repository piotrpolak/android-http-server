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
    private String name;

    private static final String nameStart = "name=\"";
    private static final String fileNameStart = "filename=\"";

//    private static final String[] ALLOWED_CONTENT_DISPOSITIONS = {"inline",
//            "attachment",
//            "form-data",
//            "signal",
//            "alert",
//            "icon",
//            "render",
//            "recipient-list-history",
//            "session",
//            "aib",
//            "early-session",
//            "recipient",
//            "notification",
//            "by-reference",
//            "info-package",
//            "recording-session"
//    };


    /**
     * Parses multipart headers
     *
     * @param headersString headers
     */
    public void parse(String headersString) {
        super.parse(headersString, false);

        String contentDispositionHeaderValue = getHeader(Headers.HEADER_CONTENT_DISPOSITION);
        if (contentDispositionHeaderValue != null) {
            String contentDispositionLower = contentDispositionHeaderValue.toLowerCase();

            int nameStartPos = contentDispositionLower.indexOf(nameStart);
            if (nameStartPos > -1) {
                String name = contentDispositionHeaderValue.substring(nameStartPos + nameStart.length());
                int quotationMarkPosition = name.indexOf("\"");
                if (quotationMarkPosition == -1) {
                    // TODO throw new MalformedHeaderException();
                    name = null;
                } else {
                    name = name.substring(0, quotationMarkPosition);
                }
                setName(name);
            }


            int fileNameStartPos = contentDispositionLower.indexOf(fileNameStart);
            if (fileNameStartPos > -1) {
                String fileName = contentDispositionHeaderValue.substring(fileNameStartPos + fileNameStart.length());
                int quotationMark2Position = fileName.indexOf("\"");

                if (quotationMark2Position == -1) {
                    // TODO throw new MalformedHeaderException();
                    fileName = null;
                } else {
                    fileName = fileName.substring(0, quotationMark2Position);
                }

                setFileName(fileName);
            }
        }
        setContentType(getHeader(Headers.HEADER_CONTENT_TYPE));
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
    public String getName() {
        return name;
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
    private void setName(String name) {
        this.name = name;
    }
}
