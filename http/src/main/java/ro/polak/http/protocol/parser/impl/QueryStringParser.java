/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import java.util.HashMap;
import java.util.Map;

import ro.polak.http.protocol.parser.Parser;

/**
 * HTTP request headers wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201609
 */
public class QueryStringParser implements Parser<Map<String, String>> {

    /**
     * Returns parsed query parameters
     *
     * @param queryString
     * @return
     */
    @Override
    public Map<String, String> parse(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        String queryParametersArray[] = queryString.split("&");
        if (queryParametersArray.length > 0) {
            for (int i = 0; i < queryParametersArray.length; i++) {
                String parameterPair[] = queryParametersArray[i].split("=", 2);

                if (parameterPair[0].length() == 0) {
                    continue;
                }

                parameters.put(parameterPair[0], ro.polak.http.utilities.Utilities.urlDecode(parameterPair[1]));
            }
        }

        return parameters;
    }
}
