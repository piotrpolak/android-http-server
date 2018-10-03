/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2017
 **************************************************/

package ro.polak.http.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * Servlet request.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201707
 */
public interface ServletRequest {

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
     * Returns the value of specified GET attribute.
     *
     * @param paramName name of the GET attribute
     * @return value of the GET attribute
     */
    String getParameter(String paramName);

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
     * Returns the remove address.
     *
     * @return string representation of remote IP
     */
    String getRemoteAddr();


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

    //  RequestDispatcher getRequestDispatcher(String path)

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
}
