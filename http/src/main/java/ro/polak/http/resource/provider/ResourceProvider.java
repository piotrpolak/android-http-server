/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.resource.provider;

import ro.polak.http.Loadable;

/**
 * Interface used for loading certain types of HTTP resources.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface ResourceProvider extends Loadable {

    /**
     * Tells whether this resource provider can load resource for given path.
     *
     * @param path
     * @return
     */
    boolean canLoad(String path);
}
