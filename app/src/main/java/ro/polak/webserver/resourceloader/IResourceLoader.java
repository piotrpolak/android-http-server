/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.resourceloader;

import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * Interface used for loading certain types of HTTP resources
 */
public interface IResourceLoader {

    /**
     * Loads the resource by URI, returns true if the resource was found or an error was served
     *
     * @param uri
     * @param request
     * @param response
     * @return
     */
    boolean load(String uri, HTTPRequest request, HTTPResponse response);
}
