/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package example.filter;

import java.io.IOException;

import ro.polak.http.exception.AccessDeniedException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * Always throws AccessDeniedException
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class FakeSecuredFilter implements Filter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        throw new AccessDeniedException();
    }
}
