/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

import ro.polak.webserver.HttpRequestHeaders;

/**
 * HTTP request
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface HttpRequest {

    /**
     * Returns the remove address
     *
     * @return string representation of remote IP
     */
    String getRemoteAddr();

    /**
     * @return FileUpload for multipart request
     */
    FileUpload getFileUpload();

    /**
     * Returns whether the request is keep-alive
     *
     * @return true for keep-alive connections
     */
    boolean isKeepAlive();

    /**
     * Sets keep alive
     *
     * @param isKeepAlive
     */
    void setKeepAlive(boolean isKeepAlive);

    /**
     * Tells whether the request is of type MultiPart
     *
     * @return true for multipart requests
     */
    boolean isMultipart();

    /**
     * Returns headers of the request
     *
     * @return headers of the request
     */
    HttpRequestHeaders getHeaders();

    /**
     * Returns cookie of specified name
     *
     * @param cookieName name of cookie
     * @return String value of cookie
     */
    String getCookie(String cookieName);

    /**
     * Returns the value of specified GET attribute
     *
     * @param paramName name of the GET attribute
     * @return value of the GET attribute
     */
    String _get(String paramName);

    /**
     * Returns the value of specified GET attribute or the default value when no GET attribute
     *
     * @param paramName    name of the GET attribute
     * @param defaultValue
     * @return
     */
    String _get(String paramName, String defaultValue);

    /**
     * Returns the value of specified POST attribute
     *
     * @param paramName name of the POST attribute
     * @return value of the POST attribute
     */
    String _post(String paramName);

    /**
     * Returns the value of specified POST attribute or the default value when no GET attribute
     *
     * @param paramName    name of the POST attribute
     * @param defaultValue
     * @return value of the POST attribute
     */
    String _post(String paramName, String defaultValue);

    /**
     * Returns session associated with this request.
     *
     * @param create
     * @return
     */
    HttpSessionWrapper getSession(boolean create);

    /**
     * Returns session associated with this request. Creates a new session if missing
     *
     * @return
     */
    HttpSessionWrapper getSession();
}
