/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

/**
 * Cookie usage example page
 */
public class Cookie extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {

        // Saving session attribute name in a variable for convenience
        String attributeName = "pageHits";

        // Resetting the counter
        int pageHits = 0;

        // Getting the page hits from session if exists
        if (request.getCookie(attributeName) != null) {
            // Please note the session attribute is of String type
            pageHits = Integer.parseInt(request.getCookie(attributeName).getValue());
        }

        // Incrementing hits counter
        ++pageHits;

        // Persisting incremented value in session
        response.addCookie(new ro.polak.webserver.servlet.Cookie(attributeName, Integer.toString(pageHits)));

        // Printing out the result
        response.getPrintWriter().print("Cookie page hits: " + pageHits + " " + request.getCookie(attributeName).getValue());
    }
}
