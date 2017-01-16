/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.error.impl;

import ro.polak.http.error.AbstractPlainTextHttpErrorHandler;
import ro.polak.http.servlet.HttpResponse;

/**
 * 414 URI Too Long
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError414Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError414Handler() {
        super(HttpResponse.STATUS_URI_TOO_LONG, "Error 414 - URI Too Long");
    }
}
