/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/
package ro.polak.http.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.polak.http.servlet.impl.ServletContextImpl;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Utility for building servlet context.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public final class ServletContextBuilder {

    private final List<ServletMapping> servletMappings = new ArrayList<>();
    private final List<FilterMapping> filterMappings = new ArrayList<>();
    private final Map<String, Object> attributes = new HashMap<>();
    private final DeploymentDescriptorBuilder parent;
    private final SessionStorage sessionStorage;
    private final ServerConfig serverConfig;

    private String contextPath;

    /**
     * Creates a mapping builder. This constructor should be package scoped.
     *
     * @param parent
     * @param sessionStorage
     * @param serverConfig
     */
    ServletContextBuilder(final DeploymentDescriptorBuilder parent,
                          final SessionStorage sessionStorage,
                          final ServerConfig serverConfig) {
        this.parent = parent;
        this.sessionStorage = sessionStorage;
        this.serverConfig = serverConfig;
    }

    public ServletMappingBuilder addServlet() {
        return new ServletMappingBuilder(this);
    }

    public FilterMappingBuilder addFilter() {
        return new FilterMappingBuilder(this);
    }

    public ServletContextBuilder withContextPath(final String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public ServletContextBuilder withAttribute(final String name, final Object value) {
        attributes.put(name, value);
        return this;
    }

    public DeploymentDescriptorBuilder end() {
        parent.addServletContext(new ServletContextImpl(contextPath,
                servletMappings,
                filterMappings,
                attributes,
                serverConfig,
                sessionStorage
        ));
        return parent;
    }

    /**
     * Adds servlet mapping. This method should be package scoped.
     *
     * @param servletMapping
     * @return
     */
    protected ServletContextBuilder withServletMapping(final ServletMapping servletMapping) {
        servletMappings.add(servletMapping);
        return this;
    }

    /**
     * Adds servlet mapping. This method should be package scoped.
     *
     * @param filterMapping
     * @return
     */
    protected ServletContextBuilder withFilterMapping(final FilterMapping filterMapping) {
        filterMappings.add(filterMapping);
        return this;
    }
}
