/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.ServerConfig;
import ro.polak.http.session.storage.SessionStorage;
import ro.polak.http.utilities.RandomStringGenerator;
import ro.polak.http.utilities.Utilities;

/**
 * Servlet context implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServletContextWrapper implements ServletContext {

    private static final Logger LOGGER = Logger.getLogger(ServletContextWrapper.class.getName());

    private final ServerConfig serverConfig;
    private final SessionStorage sessionStorage;
    private final Map<String, Object> attributes;

    /**
     * Default constructor.
     *
     * @param serverConfig
     * @param sessionStorage
     */
    public ServletContextWrapper(ServerConfig serverConfig, SessionStorage sessionStorage) {
        this.serverConfig = serverConfig;
        this.sessionStorage = sessionStorage;
        attributes = new HashMap<>();
    }

    @Override
    public String getMimeType(String file) {
        return serverConfig.getMimeTypeMapping().
                getMimeTypeByExtension(Utilities.getExtension(file));
    }

    @Override
    public void setAttribute(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public Object getAttribute(String name) {
        if (attributes.containsKey(name)) {
            return attributes.get(name);
        }

        return null;
    }

    @Override
    public Enumeration getAttributeNames() {

        final Iterator iterator = attributes.keySet().iterator();

        return new Enumeration() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public Object nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * Gets session for the given id.
     *
     * @param id
     * @return
     */
    public HttpSessionWrapper getSession(String id) {
        HttpSessionWrapper session = null;

        try {
            session = sessionStorage.getSession(id);
            if (session != null) {
                session.setServletContext(this);

                if (isSessionExpired(session)) {
                    sessionStorage.removeSession(session);
                    LOGGER.log(Level.FINE, "Removed expired session {0}",
                            new Object[]{session.getId()});
                    session = null;
                }
            }

        } catch (IOException e) {
        }

        return session;
    }

    /**
     * Creates a new session.
     *
     * @return
     */
    public HttpSessionWrapper createNewSession() {
        HttpSessionWrapper session = new HttpSessionWrapper(RandomStringGenerator.generate());
        session.setServletContext(this);
        LOGGER.log(Level.FINE, "Created a new session {0}",
                new Object[]{session.getId()});
        return session;
    }

    /**
     * Handles session storage/invalidation, sets session cookies.
     *
     * @param session
     * @param response
     * @throws IOException
     */
    public void handleSession(HttpSessionWrapper session, HttpResponseWrapper response) throws IOException {
        Cookie cookie = new Cookie(HttpSessionWrapper.COOKIE_NAME, "");
        if (session.isInvalidated()) {
            cookie.setMaxAge(-100);

            sessionStorage.removeSession(session);
            LOGGER.log(Level.FINE, "Invalidated session {0}",
                    new Object[]{session.getId()});
        } else {
            cookie.setValue(session.getId());
            sessionStorage.persistSession(session);
        }

        response.addCookie(cookie);
    }

    private boolean isSessionExpired(HttpSessionWrapper session) {
        return System.currentTimeMillis() - session.getMaxInactiveInterval() * 1000 > session.getLastAccessedTime();
    }
}
