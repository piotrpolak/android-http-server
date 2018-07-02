/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver.base;

/**
 * Represents a configuration exception.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201710
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String s) {
        super(s);
    }

    public ConfigurationException(Throwable e) {
        super(e);
    }
}
