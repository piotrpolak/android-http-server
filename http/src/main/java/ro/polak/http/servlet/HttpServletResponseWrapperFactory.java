/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet;

import java.io.IOException;
import java.net.Socket;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;

/**
 * Utility facilitating creating new responses out of the socket.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201710
 */
public class HttpServletResponseWrapperFactory {

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
    public HttpServletResponseWrapperFactory(Serializer<Headers> headersSerializer,
                                             Serializer<Cookie> cookieHeaderSerializer,
                                             StreamHelper streamHelper) {
        this.headersSerializer = headersSerializer;
        this.cookieHeaderSerializer = cookieHeaderSerializer;
        this.streamHelper = streamHelper;
    }

    /**
     * Creates and returns a response outputStream of the socket
     *
     * @param socket
     * @return
     */
    public HttpResponseWrapper createFromSocket(Socket socket) throws IOException {
        return new HttpResponseWrapper(headersSerializer, cookieHeaderSerializer, streamHelper, socket.getOutputStream());
    }
}
