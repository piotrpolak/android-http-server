package ro.polak.webserver.controller;

import ro.polak.webserver.WebServer;
import java.lang.Object;

public interface ControllerInterface {

	public void println(String text);

	public void start();

	public void stop();

	public WebServer getServer();

	/**
	 * Returns application context, this is mostly used for android applications
	 * 
	 * @return
	 */
	public Object getContext();

	/**
	 * Sets applicatiion context, this is mostly used for android applications
	 * 
	 * @param context
	 */
	public void setContext(Object context);
}
