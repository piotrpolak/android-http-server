/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package example.filter;

import ro.polak.http.exception.AccessDeniedException;
import ro.polak.http.servlet.BasicAbstractFilter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * Always throws AccessDeniedException.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class FakeSecuredAbstractFilter extends BasicAbstractFilter {

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final FilterChain filterChain) {
        throw new AccessDeniedException();
    }
}
