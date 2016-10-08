/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import android.util.Log;

/**
 * Print writer used by servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class PrintWriter {

    public StringBuffer out = new StringBuffer();

    /**
     * Tells whether the print writer was initialized before.
     * <p/>
     * The print writes is initialized
     *
     * @return
     */
    public boolean isInitialized() {
        if (out.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Print a string
     *
     * @param s
     */
    public void print(String s) {
        out.append(s);
    }

    /**
     * Prints a boolean
     *
     * @param b
     */
    public void print(boolean b) {
        out.append(b);
    }

    /**
     * Prints an integer
     *
     * @param i
     */
    public void print(int i) {
        out.append(i);
    }

    /**
     * Prints a float
     *
     * @param f
     */
    public void print(float f) {
        out.append(f);
    }

    /**
     * Prints a character
     *
     * @param c
     */
    public void print(char c) {
        out.append(c);
    }

    /**
     * Prints an empty newline
     */
    public void println() {
        out.append("\n");
    }

    /**
     * Prints a string followed by a newline
     *
     * @param s
     */
    public void println(String s) {
        out.append(s);
        println();
    }

    /**
     * Returns the length of the buffer
     *
     * @return
     */
    public long length() {
        return out.length();
    }

    /**
     * Serializes the output buffer into the string
     *
     * @return
     */
    public String toString() {
        return out.toString();
    }

    /**
     * Writes the buffer into the response
     *
     * @param response
     */
    public void writeToResponse(HttpResponse response) {
        int bSize = 1024;

        int length = out.length(); // Total number of characters
        int current = 0; // Index of the current character

        int stat_n_chunks = (int) Math.ceil(length / bSize);
        int stat_n_chunks_real = 0;
        while (current < length) { // As long as the current element is not the
            // last element
            int end = current + bSize;
            if (end > length) {
                end = length;
            }

            // .getChars(end, end, dst, end)
            response.write(out.substring(current, end));

            current = end;

            stat_n_chunks_real++;
        }

        if (stat_n_chunks_real != stat_n_chunks) {
            Log.e("PRINTWRITER", "Number of chunks is different stat_n_chunks_real=" + stat_n_chunks_real + ", stat_n_chunks=" + stat_n_chunks);
        }

        try {
            response.flush();
        } catch (Exception e) {
        }
    }
}
