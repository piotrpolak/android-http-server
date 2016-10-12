/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.Headers;
import ro.polak.webserver.HttpRequestHeaders;
import ro.polak.webserver.MultipartRequestHandler;
import ro.polak.webserver.Statistics;
import ro.polak.webserver.controller.MainController;

/**
 * HTTP request wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpRequestWrapper implements HttpRequest {

    public final static String METHOD_CONNECT = "CONNECT";
    public final static String METHOD_DELETE = "DELETE";
    public final static String METHOD_GET = "GET";
    public final static String METHOD_HEAD = "HEAD";
    public final static String METHOD_OPTIONS = "OPTIONS";
    public final static String METHOD_PURGE = "PURGE";
    public final static String METHOD_PATCH = "PATCH";
    public final static String METHOD_POST = "POST";
    public final static String METHOD_PUT = "PUT";
    public final static String METHOD_TRACE = "TRACE";

    private HttpRequestHeaders headers;
    private boolean isKeepAlive = false;
    private boolean isMultipart = false;
    private String remoteAddress;
    private Map cookies;
    private FileUpload fileUpload;
    private HttpSessionWrapper session;
    private boolean sessionWasRequested = false;
    private ServletContextWrapper servletContext;

    /**
     * Creates and returns a request out of the socket
     *
     * @param socket
     * @return
     */
    public static HttpRequestWrapper createFromSocket(Socket socket) throws IOException {

        // FIXME Move to HttpRequestWrapperFactory

        // The request object
        HttpRequestWrapper request = new HttpRequestWrapper();
        // The headers object
        HttpRequestHeaders headers = new HttpRequestHeaders();

        // Setting remote IP
        request.setRemoteAddr(socket.getInetAddress().getHostAddress().toString());

        // StringBuilder is more efficient when the string will be accessed from a single thread
        StringBuffer inputHeadersBuffer = new StringBuffer();
        StringBuffer statusLineBuffer = new StringBuffer();

        // Reading the input stream

        // Getting the input stream out of the socket
        InputStream in = socket.getInputStream();

        // Reading the first, status line
        byte[] buffer = new byte[1];
        while (in.read(buffer, 0, buffer.length) != -1) {
            // Appending buffer as long as the last character differs from \n
            if (buffer[0] == '\n') {
                break;
            }
            statusLineBuffer.append((char) buffer[0]);
        }
        // Setting status line, getting method, URI etc
        headers.setStatus(statusLineBuffer.toString());
        Statistics.addBytesReceived(statusLineBuffer.length() + 1);


        // Resetting buffer and reading the rest of headers until \r\n
        buffer = new byte[1];
        while (in.read(buffer, 0, buffer.length) != -1) {
            // Appending input headers
            inputHeadersBuffer.append((char) buffer[0]);
            // Check if the headers length is at least 3 characters long
            if (inputHeadersBuffer.length() > 3) {
                // Getting the last 3 characters
                if (inputHeadersBuffer.substring(inputHeadersBuffer.length() - 3, inputHeadersBuffer.length()).equals("\n\r\n")) {
                    // Remove the last 3 characters
                    inputHeadersBuffer.setLength(inputHeadersBuffer.length() - 3);
                    break;
                }
            }
        }
        Statistics.addBytesReceived(inputHeadersBuffer.length() + 3);

        // Parsing headers if they are at least 3 characters long
        if (inputHeadersBuffer.length() > 3) {
            // Removing the last 3 characters and parsing the buffer
            headers.parse(inputHeadersBuffer.toString());
        }


        // For post method
        if (headers.getMethod().toUpperCase().equals(HttpRequestWrapper.METHOD_POST)) {

            // Getting the postLength
            int postLength = 0;
            // Checking whether the header exists
            if (headers.containsHeader(Headers.HEADER_CONTENT_LENGTH)) {
                try {
                    // Parsing content length
                    postLength = Integer.parseInt(headers.getHeader(Headers.HEADER_CONTENT_LENGTH));
                } catch (NumberFormatException e) {
                    // Keep 0 value - makes no sense to parse the data
                }
            }

            // Only if post length is greater than 0
            if (postLength > 0) {
                // For multipart request
                if (headers.containsHeader(Headers.HEADER_CONTENT_TYPE) && headers.getHeader(Headers.HEADER_CONTENT_TYPE).startsWith("multipart/form-data")) {
                    // Getting the boundary
                    String boundary = headers.getHeader(Headers.HEADER_CONTENT_TYPE);

                    // Getting boundary
                    String boundaryStartString = "boundary=";
                    int boundaryPosition = boundary.indexOf(boundaryStartString);

                    // Checking whether boundary= exists
                    if (boundaryPosition > -1) {
                        // Protection against illegal indexes
                        try {
                            boundary = boundary.substring(boundaryPosition + boundaryStartString.length(), boundary.length());
                            MultipartRequestHandler mrh = new MultipartRequestHandler(in, postLength, boundary, MainController.getInstance().getWebServer().getServerConfig().getTempPath());
                            mrh.handle();

                            headers.setPost(mrh.getPost());
                            request.setFileUpload(new FileUpload(mrh.getUploadedFiles()));
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                }
                // For normal requests
                else {
                    buffer = new byte[1];
                    StringBuffer postLine = new StringBuffer();
                    while (in.read(buffer, 0, buffer.length) != -1) {
                        postLine.append((char) buffer[0]);
                        if (postLine.length() >= postLength) {
                            // Forced "the end"
                            break;
                        }
                    }
                    // Setting the headers post line
                    headers.setPostLine(postLine.toString());

                    Statistics.addBytesReceived(postLine.length());
                } // end else
            } // end if length
        } // end if post

        // Assigning headers
        request.setHeaders(headers);
        // Returns the created request
        return request;
    }

    /**
     * Default constructor
     */
    public HttpRequestWrapper() {
        Statistics.addRequest();
        fileUpload = new FileUpload();
    }

    /**
     * Sets the servlet context.
     *
     * @param servletContext
     */
    public void setServletContext(ServletContextWrapper servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddress;
    }

    @Override
    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    @Override
    public void setKeepAlive(boolean isKeepAlive) {
        this.isKeepAlive = isKeepAlive;
    }

    @Override
    public boolean isMultipart() {
        return isMultipart;
    }

    @Override
    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    @Override
    public FileUpload getFileUpload() {
        return this.fileUpload;
    }

    @Override
    public String getCookie(String cookieName) {
        // Parses cookies upon request
        if (cookies == null) {
            // now parsing only for a new cookies
            cookies = new HashMap();

            // Return null when there is no cookie headers
            if (!headers.containsHeader(Headers.HEADER_COOKIE)) {
                return null;
            }

            // Splitting separate cookies array
            String cookiesStr[] = headers.getHeader(Headers.HEADER_COOKIE).split(";");
            for (int i = 0; i < cookiesStr.length; i++) {
                // Splitting cookie name=value pair
                try {
                    String cookieValues[] = cookiesStr[i].split("=");
                    cookies.put(cookieValues[0].trim(), cookieValues[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // No value or no = character
                    return null;
                }
            }
        }

        try {
            return Utilities.URLDecode((String) cookies.get(cookieName));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String _get(String paramName) {
        return headers._get(paramName);
    }

    @Override
    public String _get(String paramName, String defaultValue) {
        String value = this._get(paramName);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    @Override
    public String _post(String paramName) {
        return headers._post(paramName);
    }

    @Override
    public String _post(String paramName, String defaultValue) {
        String value = this._post(paramName);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    @Override
    public HttpSessionWrapper getSession(boolean create) {
        if (!sessionWasRequested) {
            sessionWasRequested = true;
            session = servletContext.getSession(this.getCookie(HttpSessionWrapper.COOKIE_NAME));
        }

        if (session == null && create) {
            session = servletContext.createNewSession();
        }

        if (session != null) {
            session.setLastAccessedTime(System.currentTimeMillis());
        }

        return session;
    }

    @Override
    public HttpSessionWrapper getSession() {
        return getSession(true);
    }

    /**
     * Sets the remote address
     *
     * @param remoteAddress
     */
    private void setRemoteAddr(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * Sets the request headers
     *
     * @param headers
     */
    public void setHeaders(HttpRequestHeaders headers) {
        this.headers = headers;
    }

    /**
     * Sets the file upload associated with the request
     *
     * @param fileUpload
     */
    private void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }
}
