/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import ro.polak.http.RequestStatus;
import ro.polak.http.protocol.parser.Parser;

/**
 * Utility for parsing HTTP status line.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class RequestStatusParser implements Parser<RequestStatus> {

    /**
     * Parses status line
     *
     * @param input
     * @return
     */
    @Override
    public RequestStatus parse(String input) {

        RequestStatus status = new RequestStatus();
        status.setQueryString("");
        String uri;

        String statusArray[] = input.split(" ", 3);

        if (statusArray.length < 2) {
            throw new IllegalArgumentException("Input status string too short");
        }

        // First element of the array is the HTTP method
        status.setMethod(statusArray[0].toUpperCase());
        // Second element of the array is the HTTP queryString
        uri = statusArray[1];

        // Protocol is the thrid part of the status line
        if (statusArray.length > 2) {
            status.setProtocol(statusArray[2]);
        }

        int questionMarkPosition = uri.indexOf("?");
        if (questionMarkPosition > -1) {
            status.setQueryString(uri.substring(questionMarkPosition + 1));
            uri = uri.substring(0, questionMarkPosition);
        }
        status.setUri(uri);
        return status;
    }
}
