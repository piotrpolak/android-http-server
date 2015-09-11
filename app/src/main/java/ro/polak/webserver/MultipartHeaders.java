package ro.polak.webserver;

/**
 * Headers for each multipart
 * <p/>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 */
public class MultipartHeaders extends Headers {

    public String fileName = null;
    public String contentType = null;
    public String name = null;

    /**
     * Parses multipart headers
     *
     * @param headersString headers
     */
    public MultipartHeaders(String headersString) {
        this.parse(headersString);
        String contentDisposition = this.getHeader("Content-Disposition");
        name = contentDisposition.substring(contentDisposition.indexOf("name=\"") + 6);
        try {
            name = name.substring(0, name.indexOf("\""));
        } catch (Exception e) {
            // Do nothing
        }

        contentType = this.getHeader("Content-Type");

        if (contentType != null) {
            fileName = contentDisposition.substring(contentDisposition.indexOf("filename=\"") + 10);
            fileName = fileName.substring(0, fileName.indexOf("\""));
        }
    }
}
