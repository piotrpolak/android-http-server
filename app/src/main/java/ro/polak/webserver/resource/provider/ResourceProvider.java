/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.resource.provider;

import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;

/**
 * Interface used for loading certain types of HTTP resources
 */
public interface ResourceProvider {

    /**
     * Loads the resource by URI, returns true if the resource was found or an error was served
     *
     * @param uri
     * @param request
     * @param response
     * @return
     */
    boolean load(String uri, HttpRequest request, HttpResponse response);
}
