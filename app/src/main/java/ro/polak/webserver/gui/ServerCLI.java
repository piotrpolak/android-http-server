/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.polak.webserver.gui;

import ro.polak.webserver.controller.IController;

/**
 * @author Admin
 */
public class ServerCLI implements IServerUI {

    public void initialize(IController controller) {

    }

    public void println(String text) {
        System.out.println(text);
    }

    public void stop() {
        this.println("The server has stopped.");
    }

    public void start() {
        this.println("The server has started.");
    }
}
