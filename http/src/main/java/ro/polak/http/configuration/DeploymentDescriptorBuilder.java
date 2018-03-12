/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration;

import java.util.ArrayList;
import java.util.List;

import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Utility for building servlet context configuration.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class DeploymentDescriptorBuilder {

    private final List<ServletContextWrapper> servletContextWrappers = new ArrayList<>();

    private SessionStorage sessionStorage;
    private ServerConfig serverConfig;

    /**
     * This constructor is intentionally private.
     */
    private DeploymentDescriptorBuilder() {
    }

    public static DeploymentDescriptorBuilder create() {
        return new DeploymentDescriptorBuilder();
    }

    public DeploymentDescriptorBuilder withSessionStorage(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
        return this;
    }

    public DeploymentDescriptorBuilder withServerConfig(ServerConfig serverConfig) {
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
    protected DeploymentDescriptorBuilder addServletContext(ServletContextWrapper servletContextWrapper) {
        servletContextWrapper.setAttribute(ServerConfig.class.getName(), serverConfig);
        servletContextWrappers.add(servletContextWrapper);
        return this;
    }
}
