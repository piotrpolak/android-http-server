/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import java.util.StringTokenizer;

import ro.polak.http.Headers;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;

/**
 * Parses headers string into headers representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class HeadersParser implements Parser<Headers> {

    private static final String NEW_LINE = "\r\n";

    /**
     * Parses message headers.
     *
     * @param headersString
     * @return
     * @throws MalformedInputException
     */
    @Override
    public Headers parse(String headersString) throws MalformedInputException {
        return parse(headersString, true);
    }

    /**
     * Parses message headers.
     *
     * @param headersString
     * @param joinRepeatingHeaders
     * @return
     * @throws MalformedInputException
     */
    public Headers parse(String headersString, boolean joinRepeatingHeaders) throws MalformedInputException {

        // TODO joinRepeatingHeaders should be replaced by MultiValuedMap

        Headers headers = new Headers();

        // Mandatory \r https://www.w3.org/Protocols/rfc2616/rfc2616-sec2.html#sec2.2
        StringTokenizer st = new StringTokenizer(headersString, NEW_LINE);
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
                    headers.setHeader(lastHeaderName, lastHeaderValue.toString()); // Overwrite the previous value
                }
            } else {
                // Cleans up the previous value
                lastHeaderValue.setLength(0);

                String headerLineValues[] = line.split(":", 2);

                if (headerLineValues.length < 2) {
                    continue;
                }

                lastHeaderName = headerLineValues[0];

                if (joinRepeatingHeaders && headers.containsHeader(lastHeaderName)) {
                    lastHeaderValue.append(headers.getHeader(lastHeaderName)).append(',');
                }

                lastHeaderValue.append(ltrim(headerLineValues[1].substring(0, headerLineValues[1].length())));
                headers.setHeader(lastHeaderName, lastHeaderValue.toString());
            }
        }

        return headers;
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
}
