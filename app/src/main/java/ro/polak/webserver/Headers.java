/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

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
    public void parse(String headersString) {
        String headerLines[] = headersString.split("\n");
        for (int i = 0; i < headerLines.length; i++) {
            try {
                String headerLineValues[] = headerLines[i].split(": ");
                this.setHeader(headerLineValues[0], headerLineValues[1].substring(0, headerLineValues[1].length() - 1)); // Avoid \n\r
            } catch (ArrayIndexOutOfBoundsException e) {
                // TODO Throw an exception
                //e.printStackTrace();
            }
        }
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
     * Tells whether a header of specified name exists
     *
     * @param headerName
     * @return
     */
    public boolean containsHeader(String headerName) {
        return vars.containsKey(headerName);
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
