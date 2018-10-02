/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.errorhandler;

import java.io.IOException;

import ro.polak.http.servlet.HttpServletResponse;

/**
 * IHTTPError interface defining serve method.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public interface HttpErrorHandler {

    /**
     * Serves the error page.
     *
     * @param response
     * @throws IOException
     */
    void serve(HttpServletResponse response) throws IOException;
}
