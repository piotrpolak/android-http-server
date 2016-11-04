/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import ro.polak.webserver.Headers;

/**
 * HTTP request
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface HttpRequest {

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
     * @param name
     * @return
     */
    Enumeration getHeaderNames(String name);

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
     * Returns attribute value.
     *
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * Returns the list of attributes.
     *
     * @return
     */
    Enumeration getAttributeNames();

    /**
     * Returns character encoding.
     *
     * @return
     */
    String getCharacterEncoding();

    /**
     * Returns content length.
     *
     * @return
     */
    int getContentLength();

    /**
     * Returns requested content type.
     *
     * @return
     */
    String getContentType();

    /**
     * Returns input stream.
     *
     * @return
     */
    InputStream getInputStream();

    /**
     * Returns local address.
     *
     * @return
     */
    String getLocalAddr();

    /**
     * Returns locale.
     *
     * @return
     */
    Locale getLocale();

    /**
     * Returns the list of possible locales.
     *
     * @return
     */
    Enumeration getLocales();

    /**
     * Returns local name.
     *
     * @return
     */
    String getLocalName();

    /**
     * Retusn local port.
     *
     * @return
     */
    int getLocalPort();

    /**
     * Returns parameter map.
     *
     * @return
     */
    Map getParameterMap();

    /**
     * Returns parameter names.
     *
     * @return
     */
    Enumeration getParameterNames();

    /**
     * Returns parameter values.
     *
     * @param name
     * @return
     */
    String[] getParameterValues(String name);

    /**
     * Returns protocol.
     *
     * @return
     */
    String getProtocol();

    /**
     * Returns buffered reader.
     *
     * @return
     */
    BufferedReader getReader();

    /**
     * Returns remote host.
     *
     * @return
     */
    String getRemoteHost();

    /**
     * Returns remote port.
     *
     * @return
     */
    int getRemotePort();

    //  RequestDispatcher	getRequestDispatcher(String path)

    /**
     * Returns request scheme.
     *
     * @return
     */
    String getScheme();

    /**
     * Returns server name.
     *
     * @return
     */
    String getServerName();

    /**
     * Returns server port.
     *
     * @return
     */
    int getServerPort();

    /**
     * Tells whether the request is of HTTPS type.
     *
     * @return
     */
    boolean isSecure();

    /**
     * Removes attribute.
     *
     * @param name
     */
    void removeAttribute(String name);

    /**
     * Sets attribute.
     *
     * @param name
     * @param o
     */
    void setAttribute(String name, Object o);

    /**
     * Sets character encoding.
     *
     * @param env
     */
    void setCharacterEncoding(String env);

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
     * Returns the value of specified GET attribute
     *
     * @param paramName name of the GET attribute
     * @return value of the GET attribute
     */
    String getParameter(String paramName);

    /**
     * Returns the value of specified POST attribute
     *
     * @param paramName name of the POST attribute
     * @return value of the POST attribute
     */
    String getPostParameter(String paramName);
}
