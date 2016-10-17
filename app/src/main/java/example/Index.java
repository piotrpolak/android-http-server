/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import java.io.PrintWriter;

import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

/**
 * Hello page example page
 */
public class Index extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        PrintWriter printWriter = response.getPrintWriter();
        printWriter.println("<h1>Hello World!</h1>");
        printWriter.println("<p>Demo servlet page.</p>");
        printWriter.println("<p>Remote addr: " + request.getRemoteAddr() + "</p>");
        printWriter.println("<p>Request method: " + request.getHeaders().getMethod() + "</p>");
        printWriter.println("<p>Request path: " + request.getHeaders().getPath() + "</p>");
        printWriter.println("<p>Request protocol: " + request.getHeaders().getProtocol() + "</p>");
        printWriter.println("<p>Request URI: " + request.getHeaders().getURI() + "</p>");
        printWriter.println("<p>Request status: " + request.getHeaders().getStatus() + "</p>");
        printWriter.println("<ul>");
        printWriter.println("<li><a href='Session.dhtml'>Session example</a></li>");
        printWriter.println("<li><a href='Cookie.dhtml'>Cookie example</a></li>");
        printWriter.println("<li><a href='Forbidden.dhtml'>Forbidden page example</a></li>");
        printWriter.println("</ul>");
    }
}
