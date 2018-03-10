/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/
package ro.polak.http.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Utility for building servlet context.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class ServletContextBuilder {

    private final Set<ServletMapping> servletMappings = new HashSet<>();
    private final Map<String, Object> attributes = new HashMap<>();

    private String contextPath;
    private ServletContextConfigurationBuilder parent;
    private SessionStorage sessionStorage;
    private ServerConfig serverConfig;

    /**
     * Creates a mapping builder. This constructor should be package scoped.
     *
     * @param parent
     * @param sessionStorage
     * @param serverConfig
     */
    ServletContextBuilder(ServletContextConfigurationBuilder parent,
                          SessionStorage sessionStorage,
                          ServerConfig serverConfig) {
        this.parent = parent;
        this.sessionStorage = sessionStorage;
        this.serverConfig = serverConfig;
    }

    public ServletMappingBuilder addServlet() {
        return new ServletMappingBuilder(this);
    }

    public ServletContextBuilder withContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public ServletContextBuilder withAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public ServletContextConfigurationBuilder end() {
        parent.addServletContext(new ServletContextWrapper(contextPath,
                servletMappings,
                serverConfig,
                sessionStorage,
                attributes));
        return parent;
    }

    /**
     * Adds servlet mapping. This method should be package scoped.
     *
     * @param servletMapping
     * @return
     */
    ServletContextBuilder withServletMapping(ServletMapping servletMapping) {
        servletMappings.add(servletMapping);
        return this;
    }
}
