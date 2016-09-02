/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.Servlet;

/**
 * Session usage example page
 */
public class Session extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {

        // Saving session attribute name in a variable for convenience
        String attributeName = "pageHits";

        // Resetting the counter
        int pageHits = 0;

        // Getting the page hits from session if exists
        if (this.getSession().getAttribute(attributeName) != null) {
            // Please note the session attribute is of String type
            pageHits = Integer.parseInt(this.getSession().getAttribute(attributeName));
        }

        // Incrementing hits counter
        ++pageHits;

        // Persisting incremented value in session
        this.getSession().setAttribute(attributeName, Integer.toString(pageHits));

        // Printing out the result
        response.getPrintWriter().print("Session page hits: " + pageHits);
    }
}
