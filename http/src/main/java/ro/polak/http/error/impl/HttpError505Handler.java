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
 * 505 HTTP Version Not Supported error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError505Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError505Handler() {
        super(HttpResponse.HTTP_VERSION_NOT_SUPPORTED, "Error 505 - HTTP Version Not Supported");
    }
}
