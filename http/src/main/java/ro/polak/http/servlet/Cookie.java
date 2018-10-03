/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet;

/**
 * Represents HTTP Cookie.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201010
 */
public class Cookie {

    private String comment;
    private String domain;
    private String path;
    private int maxAge = -1;
    private String name;
    private String value;
    private boolean secure;
    private boolean httpOnly;

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(final String name, final String value) throws IllegalArgumentException {
        checkNameForIllegalCharacters(name);

        this.name = name;
        this.value = value;
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(final String name, final int value) throws IllegalArgumentException {
        this(name, Integer.toString(value));
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(final String name, final long value) throws IllegalArgumentException {
        this(name, Long.toString(value));
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(final String name, final double value) throws IllegalArgumentException {
        this(name, Double.toString(value));
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(final String name, final boolean value) throws IllegalArgumentException {
        this(name, Boolean.toString(value));
    }


    private void checkNameForIllegalCharacters(final String cookieName) throws IllegalArgumentException {
        char[] illegalCharacters = {';', ' ', '\n', '\r', '\t'};
        for (char illegalChar : illegalCharacters) {
            if (cookieName.indexOf(illegalChar) > -1) {
                throw new IllegalArgumentException("Cookie name must be composed of ASCI characters");
            }
        }
    }

    /**
     * Return cookie comment.
     *
     * @return
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets cookie comment.
     *
     * @param comment
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Returns cookie domain pattern.
     *
     * @return
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets cookie domain pattern.
     *
     * @param domain
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * Returns cookie path.
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets cookie path.
     *
     * @param path
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Returns cookie max age in seconds.
     *
     * @return
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Sets cookie max age in seconds.
     * Set negative value less than -1 to remove cookie.
     *
     * @param maxAge
     */
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Returns cookie name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns cookie value.
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets cookie value.
     *
     * @param value
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Tells whether the cookie is secure.
     *
     * @return
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Sets whether the cookie is secure.
     *
     * @param secure
     */
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    /**
     * Tells whether the cookie is http only.
     *
     * @return
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * Sets whether the cookie is http only.
     *
     * @param httpOnly
     */
    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
