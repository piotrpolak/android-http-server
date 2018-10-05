/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ro.polak.http.servlet.HttpSession;
import ro.polak.http.servlet.ServletContext;

/**
 * Http session implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpSessionImpl implements HttpSession, Serializable {

    private static final long serialVersionUID = 1L;

    public static final transient String COOKIE_NAME = "JSSSESSIONID";
    private static final int SECONDS_IN_HOUR = 3600;
    private transient boolean isInvalidated = false;
    private transient ServletContext servletContext;

    private final long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval = SECONDS_IN_HOUR;
    private String id;
    private Map<String, Object> attributes;

    /**
     * @param id
     * @param creationTime
     */
    public HttpSessionImpl(final String id, final long creationTime) {
        this.id = id;
        this.attributes = new HashMap<>();
        this.creationTime = creationTime;
        this.lastAccessedTime = creationTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttribute(final String name, final Object value) throws IllegalStateException {
        checkInvalidatedSession();
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
    public Object getAttribute(final String name) throws IllegalStateException {
        checkInvalidatedSession();
        if (attributes.containsKey(name)) {
            return attributes.get(name);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration getAttributeNames() throws IllegalStateException {
        checkInvalidatedSession();

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
     * {@inheritDoc}
     */
    @Override
    public long getCreationTime() throws IllegalStateException {
        checkInvalidatedSession();

        return creationTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastAccessedTime() throws IllegalStateException {
        checkInvalidatedSession();
        return lastAccessedTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxInactiveInterval(final int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidate() throws IllegalStateException {
        checkInvalidatedSession();
        isInvalidated = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttribute(final String name) throws IllegalStateException {
        checkInvalidatedSession();
        attributes.remove(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNew() {
        checkInvalidatedSession();
        return creationTime == lastAccessedTime;
    }

    /**
     * Tells whether the session was invalidated.
     *
     * @return
     */
    public boolean isInvalidated() {
        return isInvalidated;
    }

    /**
     * Sets servlet context.
     *
     * @param servletContext
     */
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Sets last accessed time.
     *
     * @param lastAccessedTime
     */
    public void setLastAccessedTime(final long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    /**
     * Throws exception on invalidated session.
     *
     * @throws IllegalStateException
     */
    private void checkInvalidatedSession() throws IllegalStateException {
        if (isInvalidated) {
            throw new IllegalStateException("The session has been invalidated.");
        }
    }
}
