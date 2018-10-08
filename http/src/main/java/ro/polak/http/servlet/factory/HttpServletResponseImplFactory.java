/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet.factory;

import java.io.IOException;
import java.net.Socket;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

/**
 * Utility facilitating creating new responses out of the socket.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201710
 */
public class HttpServletResponseImplFactory {

    private final Serializer<Headers> headersSerializer;
    private final Serializer<Cookie> cookieHeaderSerializer;
    private final StreamHelper streamHelper;

    /**
     * Default constructor.
     *
     * @param headersSerializer
     * @param cookieHeaderSerializer
     * @param streamHelper
     */
    public HttpServletResponseImplFactory(final Serializer<Headers> headersSerializer,
                                          final Serializer<Cookie> cookieHeaderSerializer,
                                          final StreamHelper streamHelper) {
        this.headersSerializer = headersSerializer;
        this.cookieHeaderSerializer = cookieHeaderSerializer;
        this.streamHelper = streamHelper;
    }

    /**
     * Creates and returns a response outputStream of the socket.
     *
     * @param socket
     * @return
     */
    public HttpServletResponseImpl createFromSocket(final Socket socket) throws IOException {
        return new HttpServletResponseImpl(headersSerializer,
                cookieHeaderSerializer,
                streamHelper,
                socket.getOutputStream());
    }
}
