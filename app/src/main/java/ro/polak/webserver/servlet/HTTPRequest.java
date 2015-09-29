/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.*;
import java.net.Socket;
import java.util.Hashtable;

import ro.polak.utilities.*;
import ro.polak.webserver.*;

/**
 * HTTP request wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class HTTPRequest {

    // TODO Use http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html as example

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

    private HTTPRequestHeaders headers;
    private boolean isKeepAlive = false;
    private boolean isMultipart = false;
    private String remoteAddress = null;
    private Hashtable cookie = null;
    private FileUpload fileUpload = new FileUpload();


    /**
     * Creates and returns a request out of the socket
     *
     * @param socket
     * @return
     */
    public static HTTPRequest createFromSocket(Socket socket) throws IOException {

        // The request object
        HTTPRequest request = new HTTPRequest();
        // The headers object
        HTTPRequestHeaders headers = new HTTPRequestHeaders();

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
        // TODO Move POST to constant
        if (headers.getMethod().toUpperCase().equals(HTTPRequest.METHOD_POST)) {

            // Getting the postLength
            int postLength = 0;
            // Checking whether the header exists
            if (headers.containsHeader("Content-Length")) {
                try {
                    // Parsing content length
                    postLength = Integer.parseInt(headers.getHeader("Content-Length"));
                } catch (NumberFormatException e) {
                    // Keep 0 value - makes no sense to parse the data
                }
            }

            // Only if post length is greater than 0
            if (postLength > 0) {
                // For multipart request
                if (headers.containsHeader("Content-Type") && headers.getHeader("Content-Type").startsWith("multipart/form-data")) {
                    // Getting the boundary
                    String boundary = headers.getHeader("Content-Type");

                    // Getting boundary
                    String boundaryStartString = "boundary=";
                    int boundaryPosition = boundary.indexOf(boundaryStartString);

                    // Checking whether boundary= exists
                    if (boundaryPosition > -1) {
                        // Protection against illegal indexes
                        try {
                            boundary = boundary.substring(boundaryPosition + boundaryStartString.length(), boundary.length());
                            MultipartRequestHandler mrh = new MultipartRequestHandler(in, postLength, boundary);

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
    private HTTPRequest() {
        Statistics.addRequest();
    }

    /**
     * Returns the remove address
     *
     * @return string representation of remote IP
     */
    public String getRemoteAddr() {
        return remoteAddress;
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
     * Returns whether the request is keep-alive
     *
     * @return true for keep-alive connections
     */
    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    /**
     * Sets keep alive
     *
     * @param isKeepAlive
     */
    public void setKeepAlive(boolean isKeepAlive) {
        this.isKeepAlive = isKeepAlive;
    }

    /**
     * Tells whether the request is of type MultiPart
     *
     * @return true for multipart requests
     */
    public boolean isMultipart() {
        return isMultipart;
    }

    /**
     * Returns headers of the request
     *
     * @return headers of the request
     */
    public HTTPRequestHeaders getHeaders() {
        return headers;
    }

    /**
     * Sets the request headers
     *
     * @param headers
     */
    private void setHeaders(HTTPRequestHeaders headers) {
        this.headers = headers;
    }

    /**
     * @return FileUpload for multipart request
     */
    public FileUpload getFileUpload() {
        return this.fileUpload;
    }

    /**
     * Sets the file upload associated with the request
     *
     * @param fileUpload
     */
    private void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    /**
     * Returns cookie of specified name
     *
     * @param cookieName name of cookie
     * @return String value of cookie
     */
    public String getCookie(String cookieName) {
        // Parses cookies upon request
        if (cookie == null) {
            // now parsing only for a new cookies
            cookie = new Hashtable<String, String>();

            // Return null when there is no cookie headers
            if (!headers.containsHeader("Cookie")) {
                return null;
            }

            // Splitting separate cookies array
            String cookiesStr[] = headers.getHeader("Cookie").split(";");
            for (int i = 0; i < cookiesStr.length; i++) {
                // Splitting cookie name=value pair
                try {
                    String cookieValues[] = cookiesStr[i].split("=");
                    cookie.put(cookieValues[0].trim(), cookieValues[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // No value or no = character
                    return null;
                }
            }
        }

        try {
            return Utilities.URLDecode((String) cookie.get(cookieName));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the value of specified GET attribute
     *
     * @param paramName name of the GET attribute
     * @return value of the GET attribute
     */
    public String _get(String paramName) {
        return headers._get(paramName);
    }

    /**
     * Returns the value of specified GET attribute or the default value when no GET attribute
     *
     * @param paramName    name of the GET attribute
     * @param defaultValue
     * @return
     */
    public String _get(String paramName, String defaultValue) {
        String value = this._get(paramName);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * Returns the value of specified POST attribute
     *
     * @param paramName name of the POST attribute
     * @return value of the POST attribute
     */
    public String _post(String paramName) {
        return headers._post(paramName);
    }

    /**
     * Returns the value of specified POST attribute or the default value when no GET attribute
     *
     * @param paramName    name of the POST attribute
     * @param defaultValue
     * @return value of the POST attribute
     */
    public String _post(String paramName, String defaultValue) {
        String value = this._post(paramName);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }
}
