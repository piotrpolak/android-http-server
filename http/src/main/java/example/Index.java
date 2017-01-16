/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-201
 **************************************************/

package example;

import java.io.PrintWriter;

import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

/**
 * Hello page example page
 */
public class Index extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        PrintWriter printWriter = response.getPrintWriter();
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
        printWriter.println("<ul>");
        printWriter.println("<li><a href='Session.dhtml'>Session example</a></li>");
        printWriter.println("<li><a href='Cookies.dhtml'>Cookies example</a></li>");
        printWriter.println("<li><a href='Forbidden.dhtml'>Forbidden page example</a></li>");
        printWriter.println("<li><a href='NotFound.dhtml'>Not found page example</a></li>");
        printWriter.println("<li><a href='InternalServerError.dhtml'>Internal server error page example</a></li>");
        printWriter.println("<li><a href='Streaming.dhtml'>Streaming</a></li>");
        printWriter.println("</ul>");
    }
}
