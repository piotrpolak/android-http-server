/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration;

import java.util.ArrayList;
import java.util.List;

import ro.polak.http.servlet.impl.ServletContextImpl;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Utility for building servlet context configuration.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public final class DeploymentDescriptorBuilder {

    private final List<ServletContextImpl> servletContextImpls = new ArrayList<>();

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

    public DeploymentDescriptorBuilder withSessionStorage(final SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
        return this;
    }

    public DeploymentDescriptorBuilder withServerConfig(final ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public ServletContextBuilder addServletContext() {
        return new ServletContextBuilder(this, sessionStorage, serverConfig);
    }

    public List<ServletContextImpl> build() {
        return servletContextImpls;
    }

    /**
     * Adds a servlet context. This method should be package scoped.
     *
     * @param servletContextImpl
     * @return
     */
    protected DeploymentDescriptorBuilder addServletContext(final ServletContextImpl servletContextImpl) {
        servletContextImpl.setAttribute(ServerConfig.class.getName(), serverConfig);
        servletContextImpls.add(servletContextImpl);
        return this;
    }
}
