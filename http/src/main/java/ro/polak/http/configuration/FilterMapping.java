/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration;

import java.util.regex.Pattern;

import ro.polak.http.servlet.Filter;

/**
 * Represents Filter to URL patter mapping.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public interface FilterMapping {

    /**
     * Returns registration URL pattern.
     *
     * @return
     */
    Pattern getUrlPattern();

    /**
     * Returns registration excluded URL pattern.
     *
     * @return
     */
    Pattern getUrlExcludePattern();

    /**
     * Returns mapped servlet class.
     *
     * @return
     */
    Class<? extends Filter> getFilterClass();
}
