/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP request headers wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201609
 */
public class QueryStringParser {

    /**
     * Returns parsed query parameters
     *
     * @param queryString
     * @return
     */
    public Map<String, String> parse(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        String queryParametersArray[] = queryString.split("&");
        if (queryParametersArray.length > 0) {
            for (int i = 0; i < queryParametersArray.length; i++) {
                String parameterPair[] = queryParametersArray[i].split("=", 2);
                if (parameterPair.length == 0) {
                    continue;
                }

                if (parameterPair[0].length() == 0) {
                    continue;
                }

                if (parameterPair.length > 1) {
                    parameters.put(parameterPair[0], ro.polak.utilities.Utilities.URLDecode(parameterPair[1]));
                } else {
                    parameters.put(parameterPair[0], "");
                }
            }
        }

        return parameters;
    }
}
