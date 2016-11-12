/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Servlet context.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface ServletContext {

    /**
     * Returns the MIME type of the specified file, or null if the MIME type is not known.
     *
     * @param file
     * @return
     */
    String getMimeType(String file);
}
