/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.ServletContext;
import ro.polak.http.session.storage.SessionStorage;
import ro.polak.http.utilities.FileUtilities;
import ro.polak.http.utilities.StringUtilities;

/**
 * Servlet context implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServletContextImpl implements ServletContext {

    private static final Logger LOGGER = Logger.getLogger(ServletContextImpl.class.getName());
    private static final int MS_IN_SECOND = 1000;
    public static final int MAX_AGE_IN_PAST = -100;

    private final ServerConfig serverConfig;
    private final SessionStorage sessionStorage;
    private final String contextPath;
    private final List<ServletMapping> servletMappings;
    private final List<FilterMapping> filterMappings;
    private final Map<String, Object> attributes;

    /**
     * Default constructor.
     *
     * @param contextPath
     * @param servletMappings
     * @param filterMappings
     * @param attributes
     * @param serverConfig
     * @param sessionStorage
     */
    public ServletContextImpl(final String contextPath,
                              final List<ServletMapping> servletMappings,
                              final List<FilterMapping> filterMappings,
                              final Map<String, Object> attributes,
                              final ServerConfig serverConfig,
                              final SessionStorage sessionStorage) {
        this.filterMappings = new ArrayList<>(filterMappings);
        this.serverConfig = serverConfig;
        this.sessionStorage = sessionStorage;
        this.contextPath = contextPath;
        this.servletMappings = new ArrayList<>(servletMappings);
        this.attributes = new HashMap<>(attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType(final String file) {
        return serverConfig.getMimeTypeMapping().
                getMimeTypeByExtension(FileUtilities.getExtension(file));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttribute(final String name, final Object value) {
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAttribute(final String name) {
        if (attributes.containsKey(name)) {
            return attributes.get(name);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
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
    public HttpSessionImpl getSession(final String id) {
        HttpSessionImpl session = null;

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
    public HttpSessionImpl createNewSession() {
        HttpSessionImpl session = new HttpSessionImpl(StringUtilities.generateRandom(), System.currentTimeMillis());
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
    public void handleSession(final HttpSessionImpl session, final HttpServletResponseImpl response) throws IOException {
        Cookie cookie = new Cookie(HttpSessionImpl.COOKIE_NAME, "");
        if (session.isInvalidated()) {
            cookie.setMaxAge(MAX_AGE_IN_PAST);

            sessionStorage.removeSession(session);
            LOGGER.log(Level.FINE, "Invalidated session {0}",
                    new Object[]{session.getId()});
        } else {
            cookie.setValue(session.getId());
            sessionStorage.persistSession(session);
        }

        response.addCookie(cookie);
    }

    private boolean isSessionExpired(final HttpSessionImpl session) {
        return System.currentTimeMillis() - session.getMaxInactiveInterval() * MS_IN_SECOND > session.getLastAccessedTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ServletMapping> getServletMappings() {
        return servletMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FilterMapping> getFilterMappings() {
        return filterMappings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContextPath() {
        return contextPath;
    }
}
