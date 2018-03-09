/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.configuration;

/**
 * Server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public interface ServerConfigFactory {

    /**
     * Produces and returns server config.
     *
     * @return
     */
    ServerConfig getServerConfig();
}
