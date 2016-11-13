/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * ChunkedPrintWriter
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @url https://en.wikipedia.org/wiki/Chunked_transfer_encoding
 * @since 201010
 */
public class ChunkedPrintWriter extends PrintWriter {

    private static String NEW_LINE = "\r\n";
    private static String END_LINE = "0\r\n\r\n";

    /**
     * Default constructor.
     *
     * @param out
     */
    public ChunkedPrintWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void println() {
        // Overwrites the original new line character
        synchronized (lock) {
            print(NEW_LINE);
        }
    }

    @Override
    public void write(String str) {
        String head = Long.toHexString(str.length()).toUpperCase() + NEW_LINE;
        super.write(head);
        super.write(str);
        super.write(NEW_LINE);
    }

    /**
     * Writes the end of chunked message.
     */
    public void writeEnd() {
        super.write(END_LINE);
    }
}
