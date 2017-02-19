/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import java.util.Date;

import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

/**
 * Cookie usage example page
 */
public class Cookies extends Servlet {

    private final static String PAGE_HITS_COOKIE_NAME = "page_hits";
    private final static String FIRST_VISITED_AT_COOKIE_NAME = "first_visited_at";

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        int pageHits = 0;
        if (request.getCookie(PAGE_HITS_COOKIE_NAME) != null) {
            pageHits = Integer.parseInt(request.getCookie(PAGE_HITS_COOKIE_NAME).getValue());
        }
        ++pageHits;
        response.addCookie(new Cookie(PAGE_HITS_COOKIE_NAME, pageHits));

        String firstVisitedAt;
        if (request.getCookie(FIRST_VISITED_AT_COOKIE_NAME) != null) {
            firstVisitedAt = request.getCookie(FIRST_VISITED_AT_COOKIE_NAME).getValue();
        } else {
            firstVisitedAt = (new Date()).toString();
            response.addCookie(new Cookie(FIRST_VISITED_AT_COOKIE_NAME, firstVisitedAt));
        }

        response.getPrintWriter().println("<p>Cookie page hits: " + pageHits + "</p>");
        response.getPrintWriter().println("<p>First visited at: " + firstVisitedAt + "</p>");
    }
}
