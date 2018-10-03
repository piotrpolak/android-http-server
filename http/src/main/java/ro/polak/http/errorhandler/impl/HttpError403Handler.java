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
 * 403 Forbidden HTTP error handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError403Handler extends AbstractHtmlErrorHandler {

    public HttpError403Handler(final String errorDocumentPath) {
        super(HttpServletResponse.STATUS_ACCESS_DENIED, "Error 403 - Forbidden", "<p>Access Denied.</p>",
                errorDocumentPath);
    }
}
