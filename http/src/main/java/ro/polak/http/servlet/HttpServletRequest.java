/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2017
 **************************************************/

package ro.polak.http.servlet;

import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;

import ro.polak.http.Headers;

/**
 * HTTP request
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface HttpServletRequest extends ServletRequest {

    /**
     * Returns the name of the authentication scheme used to protect the servlet.
     *
     * @return
     */
    String getAuthType();

    /**
     * Returns the portion of the request URI that indicates the context of the request.
     *
     * @return
     */
    String getContextPath();

    /**
     * Returns any extra path information after the servlet name but before the query string, and translates it to a real path.
     *
     * @return
     */
    String getPathTranslated();

    /**
     * Returns any extra path information associated with the URL the client sent when it made this request.
     *
     * @see https://stackoverflow.com/questions/4931323/whats-the-difference-between-getrequesturi-and-getpathinfo-methods-in-httpservl
     *
     * @return
     */
    String getPathInfo();

    /**
     * Returns the login of the user making this request, if the user has been authenticated, or null if the user has not been authenticated.
     *
     * @return
     */
    String getRemoteUser();

    /**
     * Returns a java.security.Principal object containing the name of the current authenticated user.
     *
     * @return
     */
    Principal getUserPrincipal();

    /**
     * Checks whether the requested session ID came in as a cookie.
     *
     * @return
     */
    boolean isRequestedSessionIdFromCookie();

    /**
     * Checks whether the requested session ID came in as part of the request URL.
     *
     * @return
     */
    boolean isRequestedSessionIdFromURL();

    /**
     * Checks whether the requested session ID is still valid.
     *
     * @return
     */
    boolean isRequestedSessionIdValid();

    /**
     * Returns a boolean indicating whether the authenticated user is included in the specified logical "role".
     *
     * @param role
     * @return
     */
    boolean isUserInRole(String role);

    /**
     * Returns request URI.
     * <p/>
     * Example: GET /test/abc.html HTTP/1.1 will return /test/abc.html
     * Example: GET /test/abc.html?a=1&b=2 HTTP/1.1 will return /test/abc.html
     *
     * @return
     */
    String getRequestURI();

    /**
     * Returns full request URL including scheme, host, port, uri BUT does not contain query string.
     * <p/>
     * Example: http://www.example.com:8080/somefile.html
     *
     * @return
     */
    StringBuilder getRequestURL();

    /**
     * Returns request method.
     *
     * @return
     */
    String getMethod();

    /**
     * Returns a header value of the specified name.
     *
     * @param name
     * @return
     */
    String getHeader(String name);

    /**
     * Returns a header int value of the specified name.
     *
     * @param name
     * @return
     */
    int getIntHeader(String name);

    /**
     * Returns a header date value of the specified name.
     *
     * @param name
     * @return
     */
    long getDateHeader(String name);

    /**
     * Returns enumeration representing header names.
     *
     * @return
     */
    Enumeration getHeaderNames();

    /**
     * Returns cookies array.
     *
     * @return
     */
    Cookie[] getCookies();

    /**
     * Returns raw query string.
     *
     * @return
     */
    String getQueryString();

    /**
     * Returns requested session id.
     *
     * @return
     */
    String getRequestedSessionId();

    /**
     * Returns session associated with this request.
     *
     * @param create
     * @return
     */
    HttpSession getSession(boolean create);

    /**
     * Returns session associated with this request. Creates a new session if missing
     *
     * @return
     */
    HttpSession getSession();

    /**
     * @return collection of UploadedFiles for multipart request
     */
    Collection<UploadedFile> getUploadedFiles();


    // -- Extensions

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
     * @deprecated
     */
    Headers getHeaders();

    /**
     * Returns cookie of specified name
     *
     * @param cookieName name of cookie
     * @return
     */
    Cookie getCookie(String cookieName);

    /**
     * Returns the value of specified POST attribute
     *
     * @param paramName name of the POST attribute
     * @return value of the POST attribute
     */
    String getPostParameter(String paramName);
}
