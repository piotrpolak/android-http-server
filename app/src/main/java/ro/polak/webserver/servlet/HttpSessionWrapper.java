/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Http session implementation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpSessionWrapper implements HttpSession, Serializable {

    public transient static final String COOKIE_NAME = "JSSSESSIONID";
    private transient boolean isInvalidated = false;
    private transient ServletContext servletContext;
    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval = 3600;
    private String id;
    private Map<String, Object> attributes;

    /**
     * @param id
     */
    public HttpSessionWrapper(String id) {
        this.id = id;
        attributes = new HashMap<>();
        creationTime = lastAccessedTime = System.currentTimeMillis();
    }

    @Override
    public void setAttribute(String name, Object value) throws IllegalStateException {
        checkInvalidatedSession();
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public Object getAttribute(String name) throws IllegalStateException {
        checkInvalidatedSession();
        if (attributes.containsKey(name)) {
            return attributes.get(name);
        }

        return null;
    }

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

    @Override
    public long getCreationTime() throws IllegalStateException {
        checkInvalidatedSession();

        return creationTime;
    }

    @Override
    public String getId() {
//        checkInvalidatedSession();
        return id;
    }

    @Override
    public long getLastAccessedTime() throws IllegalStateException {
        checkInvalidatedSession();
        return lastAccessedTime;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public void invalidate() throws IllegalStateException {
        checkInvalidatedSession();
        isInvalidated = true;
    }

    @Override
    public void removeAttribute(String name) throws IllegalStateException {
        checkInvalidatedSession();
        attributes.remove(name);
    }

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
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Sets last accessed time.
     *
     * @param lastAccessedTime
     */
    public void setLastAccessedTime(long lastAccessedTime) {
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
