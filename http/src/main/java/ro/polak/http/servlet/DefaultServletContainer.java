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

/**
 * Manages life cycle of servlets.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public class DefaultServletContainer implements ServletContainer {

    private final Map<Class<? extends HttpServlet>, Servlet> servlets = new ConcurrentHashMap<>();
    private final Map<Class<? extends HttpServlet>, ServletStats> servletStats = new ConcurrentHashMap<>();

    // TODO Implement timeout

    @Override
    public Servlet getForClass(Class<? extends HttpServlet> servletClassName, ServletConfig servletConfig)
            throws ServletInitializationException, ServletException {

        if (servlets.containsKey(servletClassName)) {
            servletStats.get(servletClassName).setLastRequestedAt(new Date());
            return servlets.get(servletClassName);
        }

        return initializeServlet(servletClassName, servletConfig);
    }

    private Servlet initializeServlet(Class<? extends HttpServlet> serverClass, ServletConfig servletConfig)
            throws ServletInitializationException, ServletException {
        Servlet servlet = instantiateServlet(serverClass);
        servlet.init(servletConfig);
        servlets.put(serverClass, servlet);
        servletStats.put(serverClass, new ServletStats());
        return servlet;
    }

    private Servlet instantiateServlet(Class<? extends HttpServlet> serverClass) throws ServletInitializationException {
        try {
            return serverClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletInitializationException(e);
        }
    }

    /**
     * Destroys all initialized servlets.
     */
    public void shutdown() {
        for (Map.Entry<Class<? extends HttpServlet>, Servlet> entry : servlets.entrySet()) {
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
    public Map<Class<? extends HttpServlet>, ServletStats> getServletStats() {
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
