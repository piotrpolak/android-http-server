/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-201
 **************************************************/

package example;

import java.io.PrintWriter;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

/**
 * Hello page example page.
 */
public class IndexServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<h1>Hello World!</h1>");
        printWriter.println("<p>Demo servlet page.</p>");
        printWriter.println("<p>Character encoding: " + request.getCharacterEncoding() + "</p>");
        printWriter.println("<p>Content length: " + request.getContentLength() + "</p>");
        printWriter.println("<p>Local addr: " + request.getLocalAddr() + "</p>");
        printWriter.println("<p>Local port: " + request.getLocalPort() + "</p>");
        printWriter.println("<p>Local port: " + request.getLocalName() + "</p>");
        printWriter.println("<p>Remote addr: " + request.getRemoteAddr() + "</p>");
        printWriter.println("<p>Remote host: " + request.getRemoteHost() + "</p>");
        printWriter.println("<p>Remote port: " + request.getRemotePort() + "</p>");
        printWriter.println("<p>Request method: " + request.getMethod() + "</p>");
        printWriter.println("<p>Request protocol: " + request.getProtocol() + "</p>");
        printWriter.println("<p>Request scheme: " + request.getScheme() + "</p>");
        printWriter.println("<p>Request method: " + request.getMethod() + "</p>");
        printWriter.println("<p>Request server name: " + request.getServerName() + "</p>");
        printWriter.println("<p>Request server port: " + request.getServerPort() + "</p>");
        printWriter.println("<p>Request is secure: " + request.isSecure() + "</p>");
        printWriter.println("<p>Request URI: " + request.getRequestURI() + "</p>");
        printWriter.println("<p>Request URL: " + request.getRequestURL() + "</p>");
        printWriter.println("<p>Auth type: " + request.getAuthType() + "</p>");
        printWriter.println("<p>Context path: " + request.getContextPath() + "</p>");
        printWriter.println("<p>Path translated: " + request.getPathTranslated() + "</p>");
        printWriter.println("<p>Path info: " + request.getPathInfo() + "</p>");
        printWriter.println("<p>Remote user: " + request.getRemoteUser() + "</p>");
        printWriter.println("<p>User principal: " + request.getUserPrincipal() + "</p>");
        printWriter.println("<p>Is requested session id from cookie: " + request.isRequestedSessionIdFromCookie() + "</p>");
        printWriter.println("<p>Is requested session id from URL: " + request.isRequestedSessionIdFromURL() + "</p>");
        printWriter.println("<p>Is requested session id valid " + request.isRequestedSessionIdValid() + "</p>");
        printWriter.println("<ul>");
        printWriter.println("<li><a href='Session'>Session example</a></li>");
        printWriter.println("<li><a href='Cookies'>Cookies example</a></li>");
        printWriter.println("<li><a href='Forbidden'>Forbidden page example</a></li>");
        printWriter.println("<li><a href='NotFound'>Not found page example</a></li>");
        printWriter.println("<li><a href='InternalServerError'>Internal server error page example</a></li>");
        printWriter.println("<li><a href='Streaming'>Streaming</a></li>");
        printWriter.println("<li><a href='Chunked'>Chunked</a></li>");
        printWriter.println("<li><a href='ChunkedWithDelay'>Chunked with a delay</a></li>");
        printWriter.println("</ul>");
    }
}
