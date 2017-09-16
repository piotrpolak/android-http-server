/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet;

/**
 * Used to map request path to servlet class name.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public interface ServletPathTranslator {

    /**
     * Returns class name for given path.
     *
     * @param requestPath
     * @return
     */
    String toClassName(String requestPath);
}
