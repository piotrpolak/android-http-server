package ro.polak.webserver.controller;

import ro.polak.webserver.WebServer;

import java.lang.Object;

/**
 * Controller Interface
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 2001012
 */
public interface IController {

    /**
     * Prints the log line into the specific output
     *
     * @param text
     */
    void println(String text);

    /**
     * Starts the server logic
     */
    void start();

    /**
     * Stops the server logic
     */
    void stop();

    /**
     * Returns the server thread
     *
     * @return
     */
    WebServer getServer();

    /**
     * Returns application context, this is mostly used for android applications
     *
     * @return
     */
    Object getContext();

    /**
     * Sets application context, this is mostly used for android applications
     *
     * @param context
     */
    void setContext(Object context);
}
