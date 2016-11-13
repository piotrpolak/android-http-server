/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.error;

import java.io.IOException;

import ro.polak.http.servlet.HttpResponse;

/**
 * IHTTPError interface defining serve method
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public interface HttpError {

    /**
     * Serves the error page.
     *
     * @param response
     * @throws IOException
     */
    void serve(HttpResponse response) throws IOException;
}
