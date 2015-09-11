package ro.polak.webserver.controller;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.WebServer;
import ro.polak.webserver.gui.*;

/**
 * The main GUI Controller
 * 
 * @author Piotr Polak
 * 
 */
public class MainController implements ControllerInterface {

	private WebServer httpServer;
	private IServerUI gui;
	private Object context;
	private static MainController instance;

	private MainController() {

	}

	public static MainController getInstance() {
		if (MainController.instance == null) {
			MainController.instance = new MainController();
		}

		return MainController.instance;
	}

	public void setGui(IServerUI gui) {
		this.gui = gui;
	}

	public void println(String text) {
		this.gui.println(WebServer.sdf.format(new java.util.Date()) + "  -  "
				+ text);
	}

	public void start() {
		this.gui.initialize(this);
		this.httpServer = new WebServer(this);

		if (this.httpServer.startServer()) {
			this.gui.start();
		} else {
			this.gui.stop();
		}
		Utilities.clearTemp();
	}

	public void stop() {
		if (this.httpServer != null) {
			this.httpServer.stopServer();
			this.httpServer = null;
		}
		this.gui.stop();
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	public WebServer getServer() {
		return this.httpServer;
	}
}
