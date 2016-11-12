/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package example;

import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

/**
 * Session usage example page
 */
public class Session extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {

        // Saving session attribute name in a variable for convenience
        String attributeName = "pageHits";

        // Resetting the counter
        int pageHits = 0;

        // Getting the page hits from session if exists
        if (request.getSession().getAttribute(attributeName) != null) {
            // Please note the session attribute is of String type
            pageHits = Integer.parseInt((String) request.getSession().getAttribute(attributeName));
        }

        // Incrementing hits counter
        ++pageHits;

        // Persisting incremented value in session
        request.getSession().setAttribute(attributeName, Integer.toString(pageHits));

        // Printing out the result
        response.getPrintWriter().println("<p>Session page hits: " + pageHits + "</p>");
        response.getPrintWriter().println("<p>Session is new: " + request.getSession().isNew() + "</p>");
        response.getPrintWriter().println("<p>Session creation time: " + request.getSession().getCreationTime() + "</p>");
        response.getPrintWriter().println("<p>Session last accessed time: " + request.getSession().getLastAccessedTime() + "</p>");
        response.getPrintWriter().println("<p>Session max inactive interval in seconds: " + request.getSession().getMaxInactiveInterval() + "</p>");
    }
}
