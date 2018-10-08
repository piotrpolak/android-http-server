/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

import java.io.OutputStream;

/**
 * Allows flushing buffer in chunks.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @see <a href="https://en.wikipedia.org/wiki/Chunked_transfer_encoding">Chunked transfer encoding</a>
 * @since 201010
 */
public class ChunkedPrintWriter extends ServletPrintWriter {

    // TODO Move this capability to the ServletOutputStreamImpl

    private static final String NEW_LINE = "\r\n";
    private static final String END_LINE = "0\r\n\r\n";

    /**
     * Default constructor.
     *
     * @param out
     */
    public ChunkedPrintWriter(final OutputStream out) {
        super(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void println() {
        // Overwrites the original new line character
        synchronized (lock) {
            print(NEW_LINE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final String str) {
        String head = Long.toHexString(str.length()).toUpperCase() + NEW_LINE;
        super.write(head);
        super.write(str);
        super.write(NEW_LINE);
    }

    /**
     * Writes the end of chunked message.
     */
    @Override
    public void writeEnd() {
        super.writeEnd();
        super.write(END_LINE);
    }
}
