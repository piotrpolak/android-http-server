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
 * Chunked transfer with delay example.
 */
public class ChunkedWithDelayServlet extends HttpServlet {

    private static final int CHUNKS_COUNT = 100;
    private static final int SLEEP_LENGTH_IN_MS = 30;

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        response.getHeaders().setHeader(HEADER_TRANSFER_ENCODING, "chunked");
        PrintWriter printWriter = response.getWriter();
        printWriter.println("<table style='height: 40px; width: 100%; border: 0; cellspacing: 0;'>");
        printWriter.println("<tr><td style='background-color: green'></td>");
        for (int i = 0; i < CHUNKS_COUNT; i++) {
            try {
                Thread.sleep(SLEEP_LENGTH_IN_MS);
            } catch (InterruptedException e) {
            }
            printWriter.println("<td style='background-color: black'></td>");
            printWriter.flush();
        }
        printWriter.println("<tr></table>");
    }
}
