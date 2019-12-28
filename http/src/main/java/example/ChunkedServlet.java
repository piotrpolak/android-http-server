/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package example;

import java.io.PrintWriter;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

import static ro.polak.http.Headers.HEADER_TRANSFER_ENCODING;

/**
 * Chunked transfer example.
 */
public class ChunkedServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        response.getHeaders().setHeader(HEADER_TRANSFER_ENCODING, "chunked");
        PrintWriter printWriter = response.getWriter();
        printWriter.print("This is an example of chunked transfer type. ");
        printWriter.flush();
        printWriter.print("Chunked transfer type can be used when the final length of the data is not known.");
    }
}
