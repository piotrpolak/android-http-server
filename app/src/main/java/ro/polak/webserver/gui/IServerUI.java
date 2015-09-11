/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.ControllerInterface;

/**
 * 
 * @author Admin
 */
public interface IServerUI {

	/**
	 * GUI initialization method
	 * 
	 * @param controller
	 */
	public void initialize(ControllerInterface controller);

	/**
	 * GUI print debug method
	 * 
	 * @param text
	 */
	public void println(String text);

	/**
	 * GUI method called by controller on stop
	 */
	public void stop();

	/**
	 * GUI method called by controller on start
	 */
	public void start();
}
