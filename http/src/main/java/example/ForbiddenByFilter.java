/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package example;

import java.io.PrintWriter;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * Forbidden by filter page example
 */
public class ForbiddenByFilter extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        PrintWriter printWriter = response.getWriter();
        printWriter.println("Hello World!");
    }
}
