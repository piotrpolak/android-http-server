/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import ro.polak.http.exception.FilterInitializationException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.utilities.DateProvider;

/**
 * Manages life cycle of servlets.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public class ServletContainerImpl implements ServletContainer {

    private static final Logger LOGGER = Logger.getLogger(ServletContainerImpl.class.getName());

    private final Map<Class<? extends HttpServlet>, Servlet> servlets = new ConcurrentHashMap<>();
    private final Map<Class<? extends Filter>, Filter> filters = new ConcurrentHashMap<>();
    private final Map<Class<? extends HttpServlet>, ServletStats> servletStats = new ConcurrentHashMap<>();

    private final Timer timer = new Timer();
    private final DateProvider dateProvider;

    public ServletContainerImpl(final DateProvider dateProvider,
                                final long servletTimeToLiveInMs,
                                final long monitoringIntervalInMs) {
        this.dateProvider = dateProvider;

        if (monitoringIntervalInMs == 0) {
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                final long nowMs = dateProvider.now().getTime();
                Map<Class<? extends HttpServlet>, ServletStats> stats = getServletStats();

                if (stats.keySet().size() == 0) {
                    LOGGER.info("Running outdated servlets check - no servlets registered.");
                    return;
                }

                LOGGER.info("Running outdated servlets check.");

                for (Map.Entry<Class<? extends HttpServlet>, ServletStats> entry : stats.entrySet()) {
                    if (nowMs - entry.getValue().getLastRequestedAt().getTime() > servletTimeToLiveInMs) {
                        LOGGER.info("Destroying outdated servlet " + entry.getKey().getName());
                        shutdownServlet(servlets.get(entry.getKey()));
                    }
                }
            }
        }, monitoringIntervalInMs, monitoringIntervalInMs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Servlet getServletForClass(final Class<? extends HttpServlet> servletClass, final ServletConfig servletConfig)
            throws ServletInitializationException, ServletException {

        if (servlets.containsKey(servletClass)) {
            servletStats.get(servletClass).setLastRequestedAt(dateProvider.now());
            return servlets.get(servletClass);
        }

        return initializeServlet(servletClass, servletConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Filter getFilterForClass(final Class<? extends Filter> filterClass, final FilterConfig filterConfig)
            throws FilterInitializationException, ServletException {
        if (filters.containsKey(filterClass)) {
            return filters.get(filterClass);
        }

        Filter filter = instantiateFilter(filterClass);
        filter.init(filterConfig);
        filters.put(filterClass, filter);
        return filter;
    }

    private Servlet initializeServlet(final Class<? extends HttpServlet> serverClass, final ServletConfig servletConfig)
            throws ServletInitializationException, ServletException {
        Servlet servlet = instantiateServlet(serverClass);
        servlet.init(servletConfig);
        servlets.put(serverClass, servlet);
        servletStats.put(serverClass, new ServletStats());
        return servlet;
    }

    private Servlet instantiateServlet(final Class<? extends HttpServlet> serverClass)
            throws ServletInitializationException {
        try {
            return serverClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ServletInitializationException(e);
        }
    }

    private Filter instantiateFilter(final Class<? extends Filter> filterClass)
            throws FilterInitializationException {
        try {
            return filterClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new FilterInitializationException(e);
        }
    }

    /**
     * Destroys all initialized servlets.
     */
    @Override
    public void shutdown() {
        for (Map.Entry<Class<? extends HttpServlet>, Servlet> entry : servlets.entrySet()) {
            shutdownServlet(entry.getValue());
        }
        timer.cancel();
    }

    private void shutdownServlet(final Servlet servlet) {
        servlet.destroy();
        servlets.remove(servlet.getClass());
        servletStats.remove(servlet.getClass());
    }

    /**
     * Returns a copy of servlet statistics.
     *
     * @return
     */
    public Map<Class<? extends HttpServlet>, ServletStats> getServletStats() {
        return Collections.unmodifiableMap(servletStats);
    }

    /**
     * Servlet statistics DTO.
     */
    public final class ServletStats {

        private Date initializedAt;
        private Date lastRequestedAt;

        public ServletStats() {
            initializedAt = dateProvider.now();
            lastRequestedAt = dateProvider.now();
        }

        public Date getInitializedAt() {
            return new Date(initializedAt.getTime());
        }

        public Date getLastRequestedAt() {
            return new Date(lastRequestedAt.getTime());
        }

        public void setLastRequestedAt(final Date lastRequestedAt) {
            this.lastRequestedAt = lastRequestedAt;
        }
    }
}
