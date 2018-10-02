/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http;

/**
 * HTTP status representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class RequestStatus {

    private String method;
    private String uri;
    private String queryString;
    private String protocol;

    /**
     * Returns HTTP method.
     *
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets HTTP method.
     *
     * @param method
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Returns requested URI. The URI does not contain query string.
     *
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets requested URI. The URI must not contain query string.
     *
     * @param uri
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * Returns request query string.
     *
     * @return
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Sets request query string.
     *
     * @param queryString
     */
    public void setQueryString(final String queryString) {
        this.queryString = queryString;
    }

    /**
     * Returns HTTP protocol.
     *
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets HTTP protocol.
     *
     * @param protocol
     */
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
}
