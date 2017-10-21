/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.loader.ServletLoader;

/**
 * Manages life cycle of servlets.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public class DefaultServletContainer implements ServletContainer {

    private final Map<String, Servlet> servlets = new ConcurrentHashMap<>();
    private final Map<String, ServletStats> servletStats = new ConcurrentHashMap<>();
    private final ServletLoader servletLoader;

    // TODO Implement timeout

    public DefaultServletContainer(final ServletLoader servletLoader) {
        this.servletLoader = servletLoader;
    }

    @Override
    public Servlet getForClassName(String servletClassName, ServletConfig servletConfig)
            throws ServletInitializationException, ServletException {

        if (servlets.containsKey(servletClassName)) {
            servletStats.get(servletClassName).setLastRequestedAt(new Date());
            return servlets.get(servletClassName);
        }

        return initializeServlet(servletClassName, servletConfig);
    }

    private Servlet initializeServlet(String servletClassName, ServletConfig servletConfig)
            throws ServletInitializationException, ServletException {
        Servlet servlet = servletLoader.loadServlet(servletClassName);
        servlet.init(servletConfig);
        servlets.put(servletClassName, servlet);
        servletStats.put(servletClassName, new ServletStats());
        return servlet;
    }

    /**
     * Destroys all initialized servlets.
     */
    public void shutdown() {
        for (Map.Entry<String, Servlet> entry : servlets.entrySet()) {
            entry.getValue().destroy();
            servlets.remove(entry.getKey());
            servletStats.remove(entry.getKey());
        }
    }

    /**
     * Returns a copy of servlet statistics.
     *
     * @return
     */
    public Map<String, ServletStats> getServletStats() {
        return Collections.unmodifiableMap(servletStats);
    }

    public class ServletStats {

        private Date initializedAt;
        private Date lastRequestedAt;

        public ServletStats() {
            initializedAt = new Date();
            lastRequestedAt = new Date();
        }

        public Date getInitializedAt() {
            return initializedAt;
        }

        public Date getLastRequestedAt() {
            return lastRequestedAt;
        }

        public void setLastRequestedAt(Date lastRequestedAt) {
            this.lastRequestedAt = lastRequestedAt;
        }
    }
}
