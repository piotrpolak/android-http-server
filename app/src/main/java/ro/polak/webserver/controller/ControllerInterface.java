package ro.polak.webserver.controller;

import ro.polak.webserver.WebServer;

import java.lang.Object;

public interface ControllerInterface {

    void println(String text);

    void start();

    void stop();

    WebServer getServer();

    /**
     * Returns application context, this is mostly used for android applications
     *
     * @return
     */
    Object getContext();

    /**
     * Sets applicatiion context, this is mostly used for android applications
     *
     * @param context
     */
    void setContext(Object context);
}
