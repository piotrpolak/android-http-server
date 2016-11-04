/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Represents HTTP Cookie
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
    public Cookie(String name, String value) throws IllegalArgumentException {
        checkNameForIllegalCharacters(name);

        this.name = name;
        this.value = value;
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(String name, int value) throws IllegalArgumentException {
        this(name, Integer.toString(value));
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(String name, long value) throws IllegalArgumentException {
        this(name, Long.toString(value));
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(String name, double value) throws IllegalArgumentException {
        this(name, Double.toString(value));
    }

    /**
     * @param name
     * @param value
     * @throws IllegalArgumentException
     */
    public Cookie(String name, boolean value) throws IllegalArgumentException {
        this(name, Boolean.toString(value));
    }


    private void checkNameForIllegalCharacters(String name) throws IllegalArgumentException {
        char illegalCharacters[] = {';', ' ', '\n', '\r', '\t'};
        for (char illegalChar : illegalCharacters) {
            if (name.indexOf(illegalChar) > -1) {
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
    public void setComment(String comment) {
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
    public void setDomain(String domain) {
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
    public void setPath(String path) {
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
    public void setMaxAge(int maxAge) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
}
