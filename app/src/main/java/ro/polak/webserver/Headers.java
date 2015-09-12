package ro.polak.webserver;

import java.util.Hashtable;

/**
 * HTTP headers representation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class Headers {

    protected String status = "";
    protected Hashtable vars = new Hashtable<String, String>();

    /**
     * Parses headers
     *
     * @param headersString raw headers
     */
    public static Headers parse(String headersString) {

        Headers h = new Headers();

        String headerLines[] = headersString.split("\n");
        for (int i = 0; i < headerLines.length; i++) {
            try {
                String headerLineValues[] = headerLines[i].split(": ");
                h.setHeader(headerLineValues[0], headerLineValues[1].substring(0, headerLineValues[1].length() - 1)); // Avoid
                // \n\r
            } catch (ArrayIndexOutOfBoundsException e) {
                // e.printStackTrace();
            }
        }

        return h;
    }

    /**
     * Sets a header
     *
     * @param headerName  header name
     * @param headerValue header value
     */
    public void setHeader(String headerName, String headerValue) {
        vars.put(headerName, headerValue);
    }

    /**
     * Returns header's value
     *
     * @param headerName name of the header
     * @return header's value
     */
    public String getHeader(String headerName) {
        return (String) vars.get(headerName);
    }

    /**
     * Sets the status, the first line of HTTP headers
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the status, the first line of HTTP headers
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

}
