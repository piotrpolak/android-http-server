/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package admin.filter;

import java.io.IOException;

import admin.logic.AccessControl;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

import static admin.LoginServlet.RELOCATE_PARAM_NAME;

/**
 * Provides a security check before executing the servlet logic.
 */
public class SecurityFilter implements Filter {

    private FilterConfig filterConfig;
    private ServerConfig serverConfig;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        serverConfig = (ServerConfig) filterConfig.getServletContext()
                .getAttribute(ServerConfig.class.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                         final FilterChain filterChain) throws IOException, ServletException {

        AccessControl accessControl = new AccessControl(serverConfig, request.getSession());
        if (!accessControl.isLogged()) {
            String url = filterConfig.getServletContext().getContextPath() + getLoginUri(request);
            response.sendRedirect(url);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getLoginUri(final HttpServletRequest request) {
        String uri = "/Login?" + RELOCATE_PARAM_NAME + "=" + request.getRequestURI();


        if (!"".equals(request.getQueryString())) {
            uri += "?" + request.getQueryString();
        }

        return uri;
    }
}
