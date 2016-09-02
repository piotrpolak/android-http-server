/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * HTTP headers representation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201606
 * @url https://tools.ietf.org/html/rfc2616#section-4.2
 * @since 200802
 */
public class Headers {

    // TODO Make Headers to extend HashTable or another (best suited) collection class
    // TODO Refactor to MessageHeaders

    protected String status = "";
    protected Hashtable vars = new Hashtable<String, String>();

    /**
     * Parses message headers
     *
     * @param headersString raw headers
     */
    public void parse(String headersString) {

        // TODO refactor to public Headers parse(String headersString)

        // Mandatory \r https://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2.2
        StringTokenizer st = new StringTokenizer(headersString, "\r\n");
        String lastHeaderName = null;
        StringBuffer lastHeaderValue = new StringBuffer();

        while (st.hasMoreElements()) {

            String line = st.nextToken();
            char firstChar = line.charAt(0);

            // Multiline headers start with a space or a tab
            if (firstChar == ' ' || firstChar == '\t') {
                // Protection against header string starting with the space or tab character
                if (null != lastHeaderName) {
                    lastHeaderValue.append(" ");
                    lastHeaderValue.append(ltrim(line));
                    this.setHeader(lastHeaderName, ltrim(lastHeaderValue.toString())); // Overwrite the previous value
                }
            } else {
                // Cleans up the previous value
                lastHeaderValue.setLength(0);

                String headerLineValues[] = line.split(":", 2);

                if (headerLineValues.length < 2) {
                    continue;
                }

                lastHeaderName = headerLineValues[0];

                lastHeaderValue.append(headerLineValues[1].substring(0, headerLineValues[1].length()));
                this.setHeader(lastHeaderName, ltrim(lastHeaderValue.toString()));
            }
        }
    }

    /**
     * Left trims the given string.
     *
     * @param text
     * @return
     */
    private String ltrim(String text) {
        return text.replaceAll("^\\s+", "");
    }

    /**
     * Sets a header
     *
     * @param headerName  header name
     * @param headerValue header value
     */
    public void setHeader(String headerName, String headerValue) {
        vars.put(headerName.toLowerCase(), headerValue);
    }

    /**
     * Returns header's value
     *
     * @param headerName name of the header
     * @return header's value
     */
    public String getHeader(String headerName) {
        return (String) vars.get(headerName.toLowerCase());
    }

    /**
     * Tells whether a header of specified name exists
     *
     * @param headerName
     * @return
     */
    public boolean containsHeader(String headerName) {
        return vars.containsKey(headerName.toLowerCase());
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
