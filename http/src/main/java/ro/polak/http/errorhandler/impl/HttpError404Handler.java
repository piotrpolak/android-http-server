/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler.impl;

import ro.polak.http.errorhandler.AbstractHtmlErrorHandler;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * 404 File Not Found HTTP error handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError404Handler extends AbstractHtmlErrorHandler {

    public HttpError404Handler(final String errorDocumentPath) {
        super(HttpServletResponse.STATUS_NOT_FOUND, "Error 404 - File Not Found",
                "<p>The server has not found anything matching the specified URL.</p>",
                errorDocumentPath);
    }
}
