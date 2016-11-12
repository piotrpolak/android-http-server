/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import ro.polak.webserver.Headers;
import ro.polak.webserver.MultipartRequestHandler;
import ro.polak.webserver.RequestStatus;
import ro.polak.webserver.Statistics;
import ro.polak.webserver.parser.HeadersParser;
import ro.polak.webserver.parser.QueryStringParser;
import ro.polak.webserver.parser.RequestStatusParser;

/**
 * Utility facilitating creating new requests out of the socket.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class HttpRequestWrapperFactory {

    private static String BOUNDARY_START = "boundary=";

    private static HeadersParser headersParser = new HeadersParser();
    private static QueryStringParser queryStringParser = new QueryStringParser();
    private static RequestStatusParser statusParser = new RequestStatusParser();
    private static CookieParser cookieParser = new CookieParser();

    private String tempPath;

    /**
     * Default constructor.
     *
     * @param tempPath
     */
    public HttpRequestWrapperFactory(String tempPath) {
        this.tempPath = tempPath;
    }

    /**
     * Creates and returns a request out of the socket.
     *
     * @param socket
     * @return
     */
    public HttpRequestWrapper createFromSocket(Socket socket) throws IOException {
        HttpRequestWrapper request = new HttpRequestWrapper();

        InputStream in = socket.getInputStream();
        // The order matters
        RequestStatus status = statusParser.parse(getStatusLine(in));
        String headersString = getHeaders(in);
        Statistics.addBytesReceived(headersString.length() + 3);

        request.setInputStream(in);
        assignSocketMetadata(socket, request);
        request.setStatus(status);
        request.setGetParameters(queryStringParser.parse(status.getQueryString()));


        if (headersString.length() > 3) {
            request.setHeaders(headersParser.parse(headersString));
            request.setCookies(getCookies(request.getHeaders()));
        } else {
            request.setHeaders(new Headers()); // Setting implicit empty headers
        }

        if (request.getMethod().toUpperCase().equals(HttpRequestWrapper.METHOD_POST)) {
            handlePostRequest(request, in);
        }

        return request;
    }

    private void assignSocketMetadata(Socket socket, HttpRequestWrapper request) {
        // TODO Inspect values set here
        request.setSecure(false);
        request.setScheme("http");
        request.setRemoteAddr(socket.getInetAddress().getHostAddress());
        request.setRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort());
        request.setRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
        request.setLocalAddr(socket.getLocalAddress().getHostAddress());
        request.setLocalPort(socket.getLocalPort());
        request.setServerPort(socket.getLocalPort());
        request.setLocalName(socket.getLocalAddress().getHostName());
        request.setServerName(socket.getInetAddress().getHostName());
    }

    private Map<String, Cookie> getCookies(Headers headers) {
        if (headers.containsHeader(Headers.HEADER_COOKIE)) {
            return cookieParser.parse(headers.getHeader(Headers.HEADER_COOKIE));
        }
        return new HashMap<>();
    }

    private String getStatusLine(InputStream in) throws IOException {
        StringBuilder statusLine = new StringBuilder();
        byte[] buffer = new byte[1];
        while (in.read(buffer, 0, buffer.length) != -1) {
            if (buffer[0] == '\n') {
                break;
            }
            statusLine.append((char) buffer[0]);
        }
        Statistics.addBytesReceived(statusLine.length() + 1);

        return statusLine.toString();
    }

    private String getHeaders(InputStream in) throws IOException {
        StringBuilder headersString = new StringBuilder();
        byte[] buffer;
        buffer = new byte[1];
        while (in.read(buffer, 0, buffer.length) != -1) {
            // Appending input headers
            headersString.append((char) buffer[0]);
            // Check if the headers length is at least 3 characters long
            if (headersString.length() > 3) {
                // Getting the last 3 characters
                if (headersString.substring(headersString.length() - 3, headersString.length()).equals("\n\r\n")) {
                    // Remove the last 3 characters
                    headersString.setLength(headersString.length() - 3);
                    break;
                }
            }
        }

        return headersString.toString();
    }

    private void handlePostRequest(HttpRequestWrapper request, InputStream in) throws IOException {
        int postLength = 0; // TODO Consider long
        if (request.getHeaders().containsHeader(request.getHeaders().HEADER_CONTENT_LENGTH)) {
            try {
                postLength = Integer.parseInt(request.getHeaders().getHeader(request.getHeaders().HEADER_CONTENT_LENGTH));
            } catch (NumberFormatException e) {
            }
        }

        // Only if post length is greater than 0
        // Keep 0 value - makes no sense to parse the data
        if (postLength < 1) {
            return;
        }

        if (isMultipartRequest(request)) {
            handlePostMultipartRequest(request, in, postLength);
        } else {
            handlePostPlainRequest(request, in, postLength);
        }
    }

    private boolean isMultipartRequest(HttpRequestWrapper request) {
        return request.getHeaders().containsHeader(Headers.HEADER_CONTENT_TYPE)
                && request.getHeaders().getHeader(Headers.HEADER_CONTENT_TYPE).toLowerCase().startsWith("multipart/form-data");
    }

    private void handlePostPlainRequest(HttpRequestWrapper request, InputStream in, int postLength) throws IOException {
        byte[] buffer;// For non-multipart requests
        buffer = new byte[1];
        StringBuilder postLine = new StringBuilder();
        while (in.read(buffer, 0, buffer.length) != -1) {
            postLine.append((char) buffer[0]);
            if (postLine.length() >= postLength) {
                // Forced "the end"
                break;
            }
        }
        request.setPostParameters(queryStringParser.parse(postLine.toString()));
        Statistics.addBytesReceived(postLine.length());
    }

    private void handlePostMultipartRequest(HttpRequestWrapper request, InputStream in, int postLength) throws IOException {
        String boundary = request.getHeaders().getHeader(Headers.HEADER_CONTENT_TYPE);
        int boundaryPosition = boundary.toLowerCase().indexOf(BOUNDARY_START);

        // Checking whether boundary= exists
        if (boundaryPosition > -1) {
            // Protection against illegal indexes
            try {
                boundary = boundary.substring(boundaryPosition + BOUNDARY_START.length(), boundary.length());
                MultipartRequestHandler mrh = new MultipartRequestHandler(in, postLength, boundary, tempPath);
                mrh.handle();

                request.setPostParameters(mrh.getPost());
                request.setFileUpload(new FileUpload(mrh.getUploadedFiles()));
            } catch (IndexOutOfBoundsException e) {
                // TODO Refactor, avoid try catch
            }
        }
    }
}
