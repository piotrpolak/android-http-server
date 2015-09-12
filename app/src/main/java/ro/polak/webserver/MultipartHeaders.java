package ro.polak.webserver;

/**
 * Multipart request headers (for each multipart)
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 */
public class MultipartHeaders extends Headers {

    // TODO Make protected
    public String fileName = null;
    // TODO Make protected
    public String contentType = null;
    // TODO Make protected
    public String name = null;

    /**
     * Parses multipart headers
     *
     * @param headersString headers
     */
    public static MultipartHeaders parse(String headersString) {

        MultipartHeaders h = (MultipartHeaders) Headers.parse(headersString);

        String contentDisposition = h.getHeader("Content-Disposition");
        String name = contentDisposition.substring(contentDisposition.indexOf("name=\"") + 6);
        try {
            name = name.substring(0, name.indexOf("\""));
            h.setName(name);
        } catch (Exception e) {
            // Do nothing
        }

        String contentType = h.getHeader("Content-Type");

        if (contentType != null) {
            String fileName = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10);
            fileName = fileName.substring(0, fileName.indexOf("\""));
            h.setFileName(fileName);
        }

        return h;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
