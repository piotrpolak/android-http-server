/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http;

import java.io.IOException;

import ro.polak.http.servlet.impl.HttpServletRequestImpl;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

/**
 * Interface allowing to load payload into the response.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201912
 */
public interface Loadable {

    /**
     * Loads the resource for the given path by copying the stream to the response.getOutputStream().
     *
     * @param path
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    void load(String path, HttpServletRequestImpl request, HttpServletResponseImpl response) throws IOException;

    /**
     * Shuts down the resource provider if necessary, usually closes all open resources.
     */
    void shutdown();
}
