/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Print writer used by servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class PrintWriter {

    private boolean isInitialized = false;
    private StringBuilder out = new StringBuilder();

    /**
     * Tells whether this print buffer was initialized before. It might be important to distinguish
     * between an empty PrintWriter or uninitialized one when serving binary data.
     *
     * @return
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Print a string
     *
     * @param s
     */
    public void print(String s) {
        isInitialized = true;
        out.append(s);
    }

    /**
     * Prints a boolean
     *
     * @param b
     */
    public void print(boolean b) {
        isInitialized = true;
        out.append(b);
    }

    /**
     * Prints an integer
     *
     * @param i
     */
    public void print(int i) {
        isInitialized = true;
        out.append(i);
    }

    /**
     * Prints a float
     *
     * @param f
     */
    public void print(float f) {
        isInitialized = true;
        out.append(f);
    }

    /**
     * Prints a character
     *
     * @param c
     */
    public void print(char c) {
        isInitialized = true;
        out.append(c);
    }

    /**
     * Prints an empty newline
     */
    public void println() {
        isInitialized = true;
        out.append("\n");
    }

    /**
     * Prints a string followed by a newline
     *
     * @param s
     */
    public void println(String s) {
        isInitialized = true;
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
}
