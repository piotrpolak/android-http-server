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
import java.util.regex.Pattern;

import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.session.storage.SessionStorage;

/**
 * Utility for building servlet context configuration.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class ServletContextBuilder {

    private final Set<ServletMapping> servletMappings = new HashSet<>();
    private final Map<String, Object> attributes = new HashMap<>();

    private SessionStorage sessionStorage;
    private ServerConfig serverConfig;

    private ServletContextBuilder() {
    }

    public static ServletContextBuilder create() {
        return new ServletContextBuilder();
    }

    public ServletMappingBuilder addServlet() {
        return new ServletMappingBuilder(this);
    }

    public ServletContextBuilder withSessionStorage(SessionStorage sessionStorage) {
        this.sessionStorage = sessionStorage;
        return this;
    }

    public ServletContextBuilder withServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        withAttribute(ServerConfig.class.getName(), this.serverConfig);
        return this;
    }

    public ServletContextBuilder withAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public ServletContextWrapper build() {
        return new ServletContextWrapper(serverConfig, sessionStorage, servletMappings);
    }

    public static class ServletMappingBuilder {

        private final ServletContextBuilder servletContextBuilder;
        private Pattern urlPattern;
        private Class<? extends HttpServlet> servletClass;

        public ServletMappingBuilder(ServletContextBuilder servletContextBuilder) {
            this.servletContextBuilder = servletContextBuilder;
        }

        public ServletMappingBuilder withUrlPattern(Pattern urlPattern) {
            this.urlPattern = urlPattern;
            return this;
        }

        public ServletMappingBuilder withServletClass(Class<? extends HttpServlet> servletClass) {
            this.servletClass = servletClass;
            return this;
        }

        public ServletContextBuilder end() {
            servletContextBuilder.servletMappings.add(new ServletMappingImpl(urlPattern, servletClass));
            return servletContextBuilder;
        }
    }
}
