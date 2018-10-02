/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/
package ro.polak.http.servlet;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Adds additional writeEnd capability to the default PrintWriter.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 2001709
 */
public class ServletPrintWriter extends PrintWriter {

    private boolean wasWriteEndAlreadyCalled = false;

    public ServletPrintWriter(final OutputStream outputStream) {
        super(outputStream);
    }

    /**
     * Writes the end of the message. This method should be called once only.
     */
    public void writeEnd() {
        if (wasWriteEndAlreadyCalled) {
            throw new IllegalStateException("This method can be called once only.");
        }
        wasWriteEndAlreadyCalled = true;
    }
}
