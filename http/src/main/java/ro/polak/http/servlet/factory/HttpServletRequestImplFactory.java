/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2017
 **************************************************/

package ro.polak.http.servlet.factory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.polak.http.Headers;
import ro.polak.http.MultipartHeadersPart;
import ro.polak.http.MultipartRequestHandler;
import ro.polak.http.RequestStatus;
import ro.polak.http.Statistics;
import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.exception.protocol.LengthRequiredException;
import ro.polak.http.exception.protocol.MalformedOrUnsupportedMethodProtocolException;
import ro.polak.http.exception.protocol.MalformedStatusLineException;
import ro.polak.http.exception.protocol.PayloadTooLargeProtocolException;
import ro.polak.http.exception.protocol.ProtocolException;
import ro.polak.http.exception.protocol.StatusLineTooLongProtocolException;
import ro.polak.http.exception.protocol.UnsupportedProtocolException;
import ro.polak.http.exception.protocol.UriTooLongProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.impl.HttpServletRequestImpl;
import ro.polak.http.servlet.impl.ServletContextImpl;

/**
 * Utility facilitating creating new requests out of the socket.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class HttpServletRequestImplFactory {

    private static final String DEFAULT_SCHEME = "http";
    private static final int MULTIPART_BUFFER_LENGTH = 2048;

    private static final String BOUNDARY_START = "boundary=";
    private static final int URI_MAX_LENGTH = 2048;
    private static final int POST_MAX_LENGTH = 50 * 1024 * 1024;
    private static final int STATUS_MAX_LENGTH = 8 + URI_MAX_LENGTH + 9; // CONNECT + space + URI + space + HTTP/1.0
    private static final String[] RECOGNIZED_METHODS = {
            "OPTIONS",
            "GET",
            "HEAD",
            "POST",
            "PUT",
            "DELETE",
            "TRACE",
            "CONNECT"
    };
    private static final int METHOD_MAX_LENGTH;
    private static final List<String> RECOGNIZED_METHODS_LIST = Arrays.asList(RECOGNIZED_METHODS);
    private static final String HEADERS_END_DELIMINATOR = "\n\r\n";
    private static final int MINUMUM_HEADER_LINE_LENGTH = 3;
    private static final String MULTIPART_FORM_DATA_HEADER_START = "multipart/form-data";
    private static final String DEFAULT_SERVLET_CONTEXT_PATH = "/";
    private static final char NEWLINE = '\n';
    private static final String HTTP_1_0 = "HTTP/1.0";
    private static final String HTTP_1_1 = "HTTP/1.1";

    private final Parser<Headers> headersParser;
    private final Parser<Map<String, String>> queryStringParser;
    private final Parser<RequestStatus> statusParser;
    private final Parser<Map<String, Cookie>> cookieParser;

    static {
        int maxMethodLength = 0;
        for (String method : RECOGNIZED_METHODS) {
            if (method.length() > maxMethodLength) {
                maxMethodLength = method.length();
            }
        }
        METHOD_MAX_LENGTH = maxMethodLength;
    }

    private Parser<MultipartHeadersPart> multipartHeadersPartParser;
    private final String tempPath;


    /**
     * Default constructor.
     *
     * @param headersParser
     * @param queryStringParser
     * @param statusParser
     * @param cookieParser
     * @param tempPath
     */
    public HttpServletRequestImplFactory(final Parser<Headers> headersParser,
                                         final Parser<Map<String, String>> queryStringParser,
                                         final Parser<RequestStatus> statusParser,
                                         final Parser<Map<String, Cookie>> cookieParser,
                                         final Parser<MultipartHeadersPart> multipartHeadersPartParser,
                                         final String tempPath) {
        this.headersParser = headersParser;
        this.queryStringParser = queryStringParser;
        this.statusParser = statusParser;
        this.cookieParser = cookieParser;
        this.multipartHeadersPartParser = multipartHeadersPartParser;
        this.tempPath = tempPath;
    }

    /**
     * Creates and returns a request out of the socket.
     *
     * @param socket
     * @return
     */
    public HttpServletRequestImpl createFromSocket(final Socket socket)
            throws IOException, ProtocolException {

        HttpServletRequestImpl.Builder builder = HttpServletRequestImpl.createNewBuilder();

        InputStream in = socket.getInputStream();
        // The order matters

        RequestStatus status;
        try {
            status = statusParser.parse(getStatusLine(in));
        } catch (MalformedInputException e) {
            throw new MalformedStatusLineException("Malformed status line " + e.getMessage());
        }

        int uriLengthExceededWith = status.getUri().length() - URI_MAX_LENGTH;
        if (uriLengthExceededWith > 0) {
            throw new UriTooLongProtocolException("Uri length exceeded max length with"
                    + uriLengthExceededWith + " characters");
        }

        if (!isValidProtocol(status.getProtocol())) {
            throw new UnsupportedProtocolException("Protocol " + status.getProtocol() + " is not supported");
        }

        assignSocketMetadata(socket, builder);

        try {
            builder.withGetParameters(queryStringParser.parse(status.getQueryString()));
        } catch (MalformedInputException e) {
            // This should never happen
        }

        Headers headers = computeHeaders(in);

        builder.withServletContext(getImplicitServletContext())
                .withInputStream(in)
                .withStatus(status)
                .withPathTranslated(status.getUri()) // TODO There is no way to make it work under Android
                .withPathInfo("")
                .withRemoteUser(null)
                .withPrincipal(null)
                .withHeaders(headers)
                .withCookies(getCookies(headers));

        if (status.getMethod().equalsIgnoreCase(HttpServletRequest.METHOD_POST)) {
            try {
                handlePostRequest(builder, in, headers);
            } catch (MalformedInputException e) {
                throw new ProtocolException("Malformed post input");
            }
        }

        return builder.build();
    }

    /**
     * Implicit servlet context will be overwritten when running a servlet.
     *
     * @return
     */
    private ServletContextImpl getImplicitServletContext() {
        return new ServletContextImpl(DEFAULT_SERVLET_CONTEXT_PATH,
                Collections.<ServletMapping>emptyList(),
                Collections.<FilterMapping>emptyList(),
                Collections.<String, Object>emptyMap(),
                null,
                null
        );
    }

    /**
     * Reads and computes headers.
     *
     * @param in
     * @return
     * @throws IOException
     */
    private Headers computeHeaders(final InputStream in) throws IOException {
        String headersString = getHeadersString(in);
        if (headersString.length() > MINUMUM_HEADER_LINE_LENGTH) {
            try {
                return headersParser.parse(headersString);
            } catch (MalformedInputException e) {
                throw new ProtocolException("Malformed request headers");
            }
        }

        // TODO Write a test that sends a request containing status line only
        return new Headers();
    }

    private boolean isValidProtocol(final String protocol) {
        return protocol.equalsIgnoreCase(HTTP_1_0) || protocol.equalsIgnoreCase(HTTP_1_1);
    }

    private void assignSocketMetadata(final Socket socket, final HttpServletRequestImpl.Builder builder) {
        builder.withSecure(false)
                .withScheme(DEFAULT_SCHEME)
                .withRemoteAddr(socket.getInetAddress().getHostAddress())
                .withRemotePort(((InetSocketAddress) socket.getRemoteSocketAddress()).getPort())
                .withRemoteHost(((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName())
                .withLocalAddr(socket.getLocalAddress().getHostAddress())
                .withLocalPort(socket.getLocalPort()).withServerPort(socket.getLocalPort())
                .withLocalName(socket.getLocalAddress().getHostName())
                .withServerName(socket.getInetAddress().getHostName());
    }

    private Map<String, Cookie> getCookies(final Headers headers) {
        if (headers.containsHeader(Headers.HEADER_COOKIE)) {
            try {
                return cookieParser.parse(headers.getHeader(Headers.HEADER_COOKIE));
            } catch (MalformedInputException e) {
                // Returns an empty map
            }
        }
        return new HashMap<>();
    }

    private String getStatusLine(final InputStream in)
            throws IOException, StatusLineTooLongProtocolException, MalformedOrUnsupportedMethodProtocolException {
        StringBuilder statusLine = new StringBuilder();
        byte[] buffer = new byte[1];
        int length = 0;
        boolean wasMethodRead = false;
        while (in.read(buffer, 0, buffer.length) != -1) {

            ++length;

            if (buffer[0] == NEWLINE) {
                break;
            }
            statusLine.append((char) buffer[0]);

            if (!wasMethodRead) {
                if (buffer[0] == ' ') {
                    wasMethodRead = true;
                    String method = statusLine.substring(0, statusLine.length() - 1).toUpperCase();
                    if (!RECOGNIZED_METHODS_LIST.contains(method)) {
                        Statistics.addBytesReceived(length);
                        throw new MalformedOrUnsupportedMethodProtocolException("Method " + method + " is not supported");
                    }
                } else {
                    if (length > METHOD_MAX_LENGTH) {
                        Statistics.addBytesReceived(length);
                        throw new MalformedOrUnsupportedMethodProtocolException("Method name is longer than expected");
                    }
                }
            }

            if (length > STATUS_MAX_LENGTH) {
                Statistics.addBytesReceived(length);
                throw new StatusLineTooLongProtocolException("Exceeded max size of " + STATUS_MAX_LENGTH);
            }
        }
        Statistics.addBytesReceived(length);

        return statusLine.toString();
    }

    private String getHeadersString(final InputStream in) throws IOException {
        StringBuilder headersString = new StringBuilder();
        byte[] buffer;
        buffer = new byte[1];
        int headersEndSymbolLength = HEADERS_END_DELIMINATOR.length();

        while (in.read(buffer, 0, buffer.length) != -1) {
            headersString.append((char) buffer[0]);
            if (headersString.length() > headersEndSymbolLength) {
                String endChars = getEndChars(headersString, headersEndSymbolLength);
                if (endChars.equals(HEADERS_END_DELIMINATOR)) {
                    headersString.setLength(headersString.length() - headersEndSymbolLength);
                    break;
                }
            }
        }

        Statistics.addBytesReceived(headersString.length() + headersEndSymbolLength);
        return headersString.toString();
    }

    private String getEndChars(final StringBuilder headersString, final int headersEndSymbolLength) {
        return headersString.substring(headersString.length() - headersEndSymbolLength, headersString.length());
    }

    private void handlePostRequest(final HttpServletRequestImpl.Builder builder,
                                   final InputStream in,
                                   final Headers headers)
            throws IOException, MalformedInputException {
        int postLength;
        if (headers.containsHeader(Headers.HEADER_CONTENT_LENGTH)) {
            try {
                postLength = Integer.parseInt(headers.getHeader(Headers.HEADER_CONTENT_LENGTH));
            } catch (NumberFormatException e) {
                throw new MalformedInputException(e.getMessage());
            }
        } else {
            throw new LengthRequiredException();
        }

        // Only if post length is greater than 0
        // Keep 0 value - makes no sense to parse the data
        if (postLength < 1) {
            return;
        }

        if (postLength > POST_MAX_LENGTH) {
            throw new PayloadTooLargeProtocolException("Payload of " + postLength + "b exceeds the limit of "
                    + POST_MAX_LENGTH + "b");
        }

        if (isMultipartRequest(headers)) {
            handlePostMultipartRequest(builder, headers, in, postLength);
        } else {
            handlePostPlainRequest(builder, in, postLength);
        }
    }

    private boolean isMultipartRequest(final Headers headers) {
        return headers.containsHeader(Headers.HEADER_CONTENT_TYPE)
                && headers.getHeader(Headers.HEADER_CONTENT_TYPE).toLowerCase()
                .startsWith(MULTIPART_FORM_DATA_HEADER_START);
    }

    private void handlePostPlainRequest(final HttpServletRequestImpl.Builder builder,
                                        final InputStream in,
                                        final int postLength)
            throws IOException, MalformedInputException {
        byte[] buffer;
        buffer = new byte[1];
        StringBuilder postLine = new StringBuilder();
        while (in.read(buffer, 0, buffer.length) != -1) {
            postLine.append((char) buffer[0]);
            if (postLine.length() == postLength) {
                break;
            }
        }
        Statistics.addBytesReceived(postLine.length());
        builder.withPostParameters(queryStringParser.parse(postLine.toString()));
    }

    private void handlePostMultipartRequest(final HttpServletRequestImpl.Builder builder,
                                            final Headers headers,
                                            final InputStream in,
                                            final int postLength)
            throws IOException, MalformedInputException {

        String boundary = headers.getHeader(Headers.HEADER_CONTENT_TYPE);
        int boundaryPosition = boundary.toLowerCase().indexOf(BOUNDARY_START);
        builder.withMultipart(true);
        if (boundaryPosition > -1) {
            int boundaryStartPos = boundaryPosition + BOUNDARY_START.length();
            if (boundaryStartPos < boundary.length()) {
                boundary = boundary.substring(boundaryStartPos, boundary.length());
                MultipartRequestHandler mrh =
                        new MultipartRequestHandler(multipartHeadersPartParser, in, postLength, boundary,
                                tempPath, MULTIPART_BUFFER_LENGTH);
                mrh.handle();

                builder.withPostParameters(mrh.getPost()).withUploadedFiles(mrh.getUploadedFiles());
            }
        }
    }
}
