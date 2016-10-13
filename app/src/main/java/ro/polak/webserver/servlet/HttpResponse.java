/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.util.List;

import ro.polak.webserver.HttpResponseHeaders;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface HttpResponse {

    /**
     * Adds a cookie.
     *
     * @param cookie
     */
    void addCookie(Cookie cookie);

    /**
     * Returns the list of cookies.
     *
     * @return
     */
    List<Cookie> getCookies();

    /**
     * Returns a boolean indicating if the response has been committed. A
     * committed response has already had its status code and headers written.
     *
     * @return a boolean indicating if the response has been committed
     */
    boolean isCommitted();

    /**
     * Redirects the request to the specified location.
     *
     * @param location - relative or absolute path (URL)
     */
    void sendRedirect(String location);

    /**
     * Sets content type
     *
     * @param contentType content type
     */
    void setContentType(String contentType);

    /**
     * Returns the content type
     *
     * @return
     */
    String getContentType();

    /**
     * Sets keepAlive
     *
     * @param keepAlive true for keep alive connection
     */
    void setKeepAlive(boolean keepAlive);

    /**
     * Sets content length
     *
     * @param length length of content
     */
    void setContentLength(int length);

    /**
     * Sets content length
     *
     * @param length length of content
     */
    void setContentLength(long length);

    /**
     * Returns the response headers
     *
     * @return
     */
    HttpResponseHeaders getHeaders();

    /**
     * Sets status of response
     *
     * @param status status code and message
     */
    void setStatus(String status);

    /**
     * Returns request print writer
     *
     * @return request print writer
     */
    PrintWriter getPrintWriter();
}
