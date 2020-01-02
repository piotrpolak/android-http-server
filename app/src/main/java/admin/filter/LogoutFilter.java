/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package admin.filter;

import admin.logic.AccessControl;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * Handles the logout logic.
 */
public class LogoutFilter implements Filter {

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
                         final FilterChain filterChain) {

        new AccessControl(serverConfig, request.getSession()).logout();
        response.sendRedirect(filterConfig.getServletContext().getContextPath() + "/Login");
    }
}
