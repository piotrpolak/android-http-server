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

    private HTTPRequestHeaders headers;
    private boolean isKeepAlive = false;
    private boolean isMultipart = false;
    private String remoteAddress = null;
    private Hashtable _cookies = null;
    private MultipartRequestHandler mrh = null;

    /**
     * Class constructor
     *
     * @param socket - socket to be read
     */
    public HTTPRequest(Socket socket) {
        Statistics.addRequest();

        // StringBuilder is more efficient when the string will be accessed from
        // a single thread
        StringBuffer inputHeaders = new StringBuffer();
        StringBuffer statusLine = new StringBuffer();

        headers = new HTTPRequestHeaders();

        try {
            InputStream in = socket.getInputStream();

			/*
             * Reading the first, status line
			 */
            byte[] buffer = new byte[1];

            while (in.read(buffer, 0, buffer.length) != -1) {
                statusLine.append((char) buffer[0]);
                if (buffer[0] != '\n') {
                    continue;
                }
                break;
            }


            /* Seting status line, getting method, URI etc */
            headers.setStatus(statusLine.toString());
            Statistics.addBytesReceived(statusLine.length());

			/*
			 * Reading the rest of headers until \r\n
			 */
            buffer = new byte[1];

            while (in.read(buffer, 0, buffer.length) != -1) {

                inputHeaders.append((char) buffer[0]);

                if (inputHeaders.length() > 3) {
                    if (inputHeaders.substring(inputHeaders.length() - 3,
                            inputHeaders.length()).equals("\n\r\n")) {
                        break;
                    }
                }
            }

            Statistics.addBytesReceived(inputHeaders.length());

			/*
			 * Setting headers removing last 3 chars
			 */
            if (inputHeaders.length() >= 3) {
                headers.parse(inputHeaders.substring(0, inputHeaders.length() - 3));
            }
            else
            {
                headers = new HTTPRequestHeaders();
            }



			/*
			 * For post method
			 */
            try {
                if (headers.getMethod().toUpperCase().equals("POST")) {

                    int postLength = 0;
                    StringBuffer postLine = new StringBuffer();

                    try {
                        postLength = new Integer(headers.getHeader("Content-Length")).intValue();
                    } catch (Exception e) {
                    }

					/* Only if post lenght is greater than 0 */
                    if (postLength > 0) {

						/* For multipart request */
                        if (headers.getHeader("Content-Type").startsWith("multipart/form-data")) {
							/* Getting the boundary */
                            String boundary = headers.getHeader("Content-Type");
                            boundary = boundary.substring(boundary.indexOf("boundary=") + 9, boundary.length());
                            mrh = new MultipartRequestHandler(in, postLength, boundary);

                            this.headers.setPost(mrh.getPost());
                        }
						/* For normal */
                        else {
                            buffer = new byte[1];
                            while (in.read(buffer, 0, buffer.length) != -1) {
                                postLine.append((char) buffer[0]);
                                if (postLine.length() >= postLength) {
                                    break; // The end
                                }
                            }
                            headers.setPostLine(postLine.toString());

                            Statistics.addBytesReceived(postLine.length());
                        } // end else
                    } // end if lenght
                } // end if post
            } catch (NullPointerException ee) {
                try {
                    socket.close();
                } catch (Exception e2) {
                }
                return;
            }

        } catch (IOException e) {
            // Destroy current thread?

            try {
                socket.close();
            } catch (Exception e2) {
            }
            return;
        }

		/* Setting keep alive */
        try {
            if (headers != null && headers.getHeader("Connection").substring(0, 4).equals("keep")) {
                isKeepAlive = true;
            }
        } catch (Exception e) {
        }

		/* Setting remote IP */
        remoteAddress = socket.getInetAddress().getHostAddress().toString();
    }

    /**
     * @return string representation of remote IP
     */
    public String getRemoteAddr() {
        return remoteAddress;
    }

    /**
     * @return true for keep-alive connections
     */
    public boolean isKeepAlive() {
        return isKeepAlive;
    }

    /**
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
     * @return FileUpload for multipart request
     */
    public FileUpload getFileUpload() {
        if (mrh == null) {
            return new FileUpload();
        }
        return new FileUpload(mrh.getUploadedFiles());
    }

    /**
     * Returns cookie of specified name
     *
     * @param cookieName name of cookie
     * @return String value of cookie
     */
    public String getCookie(String cookieName) {
        if (_cookies == null) {
            // now parsing only for a new cookies
            _cookies = new Hashtable<String,String>();

            String cookieStr = headers.getHeader("Cookie");
            if (cookieStr == null) {
                return null;
            }

            String cookies[] = cookieStr.split(";");
            for (int i = 0; i < cookies.length; i++) {

                try {
                    String cookieValues[] = cookies[i].split("=");
                    // System.out.println("Cookie1:" + cookieValues[0] + ":" +
                    // cookieValues[1] + ":");
                    _cookies.put(cookieValues[0], cookieValues[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

        }
        try {
            return Utilities.URLDecode((String)_cookies.get(cookieName));
        } catch (Exception e) {
            e.printStackTrace();
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
     * Returns the value of specified POST attribute
     *
     * @param paramName name of the POSTT attribute
     * @return value of the POST attribute
     */
    public String _post(String paramName) {
        return headers._post(paramName);
    }
}
