/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * HTTP headers representation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @url https://tools.ietf.org/html/rfc2616#section-4.2
 * @since 200802
 */
public class Headers {

    // TODO Refactor to MessageHeaders

    public static final String HEADER_ALLOW = "Allow";
    public static final String HEADER_SERVER = "Server";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";

    protected String status = "";
    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, String> namesMap = new HashMap<>();

    /**
     * Parses message headers.
     *
     * @param headersString raw headers
     */
    public void parse(String headersString) {
        parse(headersString, true);
    }

    /**
     * Parses message headers.
     *
     * @param headersString
     * @param joinRepeatingHeaders
     */
    public void parse(String headersString, boolean joinRepeatingHeaders) {
        // Mandatory \r https://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2.2
        StringTokenizer st = new StringTokenizer(headersString, "\r\n");
        String lastHeaderName = null;
        StringBuilder lastHeaderValue = new StringBuilder();

        while (st.hasMoreElements()) {
            String line = st.nextToken();
            char firstChar = line.charAt(0);

            // Multiline headers start with a space or a tab
            if (firstChar == ' ' || firstChar == '\t') {
                // Protection against header string starting with the space or tab character
                if (null != lastHeaderName) {
                    lastHeaderValue.append(" ");
                    lastHeaderValue.append(ltrim(line));
                    setHeader(lastHeaderName, lastHeaderValue.toString()); // Overwrite the previous value
                }
            } else {
                // Cleans up the previous value
                lastHeaderValue.setLength(0);

                String headerLineValues[] = line.split(":", 2);

                if (headerLineValues.length < 2) {
                    continue;
                }

                lastHeaderName = headerLineValues[0];

                if (joinRepeatingHeaders) {
                    if (containsHeader(lastHeaderName)) {
                        lastHeaderValue.append(getHeader(lastHeaderName)).append(',');
                    }
                }

                lastHeaderValue.append(ltrim(headerLineValues[1].substring(0, headerLineValues[1].length())));
                setHeader(lastHeaderName, lastHeaderValue.toString());
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
     * @param name  header name
     * @param value header value
     */
    public void setHeader(String name, String value) {
        namesMap.put(name.toLowerCase(), name);
        headers.put(name, value);
    }

    /**
     * Returns header's value
     *
     * @param name name of the header
     * @return header's value
     */
    public String getHeader(String name) {
        return headers.get(namesMap.get(name.toLowerCase()));
    }

    /**
     * Tells whether a header of specified name exists
     *
     * @param name
     * @return
     */
    public boolean containsHeader(String name) {
        return namesMap.containsKey(name.toLowerCase());
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
