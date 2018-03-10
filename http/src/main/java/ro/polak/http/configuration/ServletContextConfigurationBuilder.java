/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Utility for building servlet context configuration.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class ServletContextConfigurationBuilder {

    private final List<ServletContextWrapper> servletContextWrappers = new ArrayList<>();

    private SessionStorage sessionStorage;
    private ServerConfig serverConfig;

    /**
     * This constructor is intentionally private.
     */
    private ServletContextConfigurationBuilder() {
    }

    public static ServletContextConfigurationBuilder create() {
        return new ServletContextConfigurationBuilder();
    }

    public ServletContextConfigurationBuilder withSessionStorage(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
        return this;
    }

    public ServletContextConfigurationBuilder withServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public ServletContextBuilder addServletContext() {
        return new ServletContextBuilder(this, sessionStorage, serverConfig);
    }

    public List<ServletContextWrapper> build() {
        return servletContextWrappers;
    }

    /**
     * Adds a servlet context. This method should be package scoped.
     *
     * @param servletContextWrapper
     * @return
     */
    ServletContextConfigurationBuilder addServletContext(ServletContextWrapper servletContextWrapper) {
        servletContextWrapper.setAttribute(ServerConfig.class.getName(), serverConfig);
        servletContextWrappers.add(servletContextWrapper);
        return this;
    }
}
