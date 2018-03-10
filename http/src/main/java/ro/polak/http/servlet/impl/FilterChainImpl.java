/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package ro.polak.http.servlet.impl;

import java.io.IOException;
import java.util.ArrayDeque;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * Default FilterChain implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class FilterChainImpl implements FilterChain {

    private ArrayDeque<Filter> filters;

    public FilterChainImpl(ArrayDeque<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        filters.pop().doFilter(request, response, this);
    }
}
