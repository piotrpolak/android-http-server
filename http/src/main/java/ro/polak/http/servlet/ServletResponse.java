/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2017
 **************************************************/

package ro.polak.http.servlet;

import java.io.PrintWriter;
import java.util.Locale;

/**
 * Servlet response.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201708
 */
public interface ServletResponse {

    /**
     * Forces any content in the buffer to be written to the client.
     */
    void flushBuffer();

    /**
     * Returns the actual buffer size used for the response.
     *
     * @return
     */
    int getBufferSize();

    /**
     * Returns the name of the character encoding (MIME charset) used for the body sent in this response.
     *
     * @return
     */
    String getCharacterEncoding();

    /**
     * Returns the content type used for the MIME body sent in this response.
     *
     * @return
     */
    String getContentType();

    /**
     * Returns the locale specified for this response using the setLocale(java.util.Locale) method.
     *
     * @return
     */
    Locale getLocale();

    /**
     * Returns a ServletOutputStream suitable for writing binary data in the response.
     *
     * @return
     */
    ServletOutputStream getOutputStream();

    /**
     * Returns a PrintWriter object that can send character text to the client.
     *
     * @return
     */
    PrintWriter getWriter();

    /**
     * Returns a boolean indicating if the response has been committed. A
     * committed response has already had its status code and headers written.
     *
     * @return a boolean indicating if the response has been committed
     */
    boolean isCommitted();

    /**
     * Clears any data that exists in the buffer as well as the status code and headers.
     */
    void reset();

    /**
     * Clears the content of the underlying buffer in the response without clearing headers or status code.
     */
    void resetBuffer();

    /**
     * Sets the preferred buffer size for the body of the response.
     *
     * @param size
     */
    void setBufferSize(int size);

    /**
     * Sets the character encoding (MIME charset) of the response being sent to the client, for example, to UTF-8.
     *
     * @param charset
     */
    void setCharacterEncoding(String charset);

    /**
     * Sets the length of the content body in the response In HTTP servlets, this method sets the HTTP Content-Length header.
     *
     * @param len
     */
    void setContentLength(int len);

    /**
     * Sets the content type of the response being sent to the client, if the response has not been committed yet.
     *
     * @param type
     */
    void setContentType(String type);

    /**
     * Sets the locale of the response, if the response has not been committed yet.
     *
     * @param loc
     */
    void setLocale(Locale loc);
}
