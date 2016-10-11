/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.session.storage;

import java.io.IOException;

import ro.polak.webserver.servlet.HttpSessionWrapper;

/**
 * Specifies methods required for storing session.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface SessionStorage {

    void persistSession(HttpSessionWrapper session) throws IOException;

    HttpSessionWrapper getSession(String id) throws IOException;

    boolean removeSession(HttpSessionWrapper session);
}
