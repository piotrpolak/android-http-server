/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

import ro.polak.webserver.HttpResponseHeaders;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface HttpResponse {

    /**
     * Sets cookie
     *
     * @param cookieName           name of the cookie
     * @param cookieValue          String value of the cookie
     * @param cookieExpiresSeconds expire time in seconds
     * @param cookiePath           path for the cookie
     */
    void setCookie(String cookieName, String cookieValue, int cookieExpiresSeconds, String cookiePath);

    /**
     * Sets cookie, expires when browser closed
     *
     * @param cookieName  name of the cookie
     * @param cookieValue String value of the cookie
     */
    void setCookie(String cookieName, String cookieValue);

    /**
     * Sets cookie
     * <p/>
     * Use negative time to remove cookie
     *
     * @param cookieName        name of the cookie
     * @param cookieValue       String value of the cookie
     * @param timeToLiveSeconds time to live in seconds
     */
    void setCookie(String cookieName, String cookieValue, int timeToLiveSeconds);

    /**
     * Removes cookie
     *
     * @param cookieName name of the cookie
     */
    void removeCookie(String cookieName);

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
