/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration.impl;

import java.util.regex.Pattern;

import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.servlet.Filter;

/**
 * Default implementation for FilterMapping.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class FilterMappingImpl implements FilterMapping {

    private final Pattern urlPattern;
    private final Pattern urlExcludePattern;
    private final Class<? extends Filter> filterClass;

    public FilterMappingImpl(final Pattern urlPattern,
                             final Pattern urlExcludePattern,
                             final Class<? extends Filter> filterClass) {
        this.urlPattern = urlPattern;
        this.urlExcludePattern = urlExcludePattern;
        this.filterClass = filterClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pattern getUrlPattern() {
        return urlPattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pattern getUrlExcludePattern() {
        return urlExcludePattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Filter> getFilterClass() {
        return filterClass;
    }
}
