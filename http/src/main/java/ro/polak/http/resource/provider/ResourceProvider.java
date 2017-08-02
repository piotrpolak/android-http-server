/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.resource.provider;

import java.io.IOException;

import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;

/**
 * Interface used for loading certain types of HTTP resources
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface ResourceProvider {

    /**
     * Tells whether this resource provider can load such a resource.
     *
     * @param path
     * @return
     */
    boolean canLoad(String path);

    /**
     * Loads the resource by URI, returns true if the resource was found or an error was served
     *
     * @param path
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    void load(String path, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException;
}
