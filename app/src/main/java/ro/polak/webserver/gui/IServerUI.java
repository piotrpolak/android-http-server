/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.IController;

/**
 * @author Admin
 */
public interface IServerUI {

    /**
     * GUI initialization method
     *
     * @param controller
     */
    void initialize(IController controller);

    /**
     * GUI print debug method
     *
     * @param text
     */
    void println(String text);

    /**
     * GUI method called by controller on stop
     */
    void stop();

    /**
     * GUI method called by controller on start
     */
    void start();
}
