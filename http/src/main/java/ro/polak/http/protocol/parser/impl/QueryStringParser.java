/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import java.util.HashMap;
import java.util.Map;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.utilities.StringUtilities;

/**
 * HTTP request headers wrapper.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201609
 */
public class QueryStringParser implements Parser<Map<String, String>> {

    private static final String PAIR_SEPARATOR = "&";
    private static final String VALUE_SEPARATOR = "=";

    /**
     * Returns parsed query parameters.
     *
     * @param queryString
     * @return
     */
    @Override
    public Map<String, String> parse(final String queryString) throws MalformedInputException {
        Map<String, String> parameters = new HashMap<>();
        String[] queryParametersArray = queryString.split(PAIR_SEPARATOR);
        for (int i = 0; i < queryParametersArray.length; i++) {
            if (queryParametersArray[i].length() == 0) {
                continue;
            }

            String[] parameterPair = queryParametersArray[i].split(VALUE_SEPARATOR, 2);
            if (parameterPair[0].length() == 0) {
                continue;
            }

            if (parameterPair.length > 1) {
                parameters.put(parameterPair[0], StringUtilities.urlDecode(parameterPair[1]));
            }
        }

        return parameters;
    }
}
