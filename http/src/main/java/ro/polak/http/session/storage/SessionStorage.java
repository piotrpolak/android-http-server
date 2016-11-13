/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.session.storage;

import java.io.IOException;

import ro.polak.http.servlet.HttpSessionWrapper;

/**
 * Specifies methods required for storing session.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface SessionStorage {

    /**
     * Persists session, throws exception in case of failure.
     *
     * @param session
     * @throws IOException
     */
    void persistSession(HttpSessionWrapper session) throws IOException;

    /**
     * Reads session for the given id. Returns null if there is no such session and throws
     * exception in case of failure.
     *
     * @param id
     * @return
     * @throws IOException
     */
    HttpSessionWrapper getSession(String id) throws IOException;

    /**
     * Removes session.
     *
     * @param session
     * @return
     */
    boolean removeSession(HttpSessionWrapper session);
}
