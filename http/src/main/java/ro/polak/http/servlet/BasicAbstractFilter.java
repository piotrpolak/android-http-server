/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2019-2020
 **************************************************/

package ro.polak.http.servlet;

/**
 * Basic filter, does not require initialization.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 202001
 */
public abstract class BasicAbstractFilter implements Filter {

    /**
     * Does nothing.
     *
     * @param filterConfig
     */
    @Override
    public void init(final FilterConfig filterConfig) {
        // Do Nothing
    }
}
