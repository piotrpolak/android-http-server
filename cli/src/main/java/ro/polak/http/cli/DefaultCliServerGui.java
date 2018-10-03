/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.cli;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

import ro.polak.http.configuration.ServerConfigFactory;
import ro.polak.http.controller.impl.ControllerImpl;
import ro.polak.http.gui.ServerGui;

/**
 * Server CLI interface along with a runner.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public class DefaultCliServerGui implements ServerGui {

    private static final Logger LOGGER = Logger.getLogger(DefaultCliServerGui.class.getName());

    /**
     * The main CLI runner method.
     *
     * @param args
     */
    public static void main(final String[] args) {
        (new DefaultCliServerGui()).init();
    }

    /**
     * Initializes the server.
     */
    public void init() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tF %1$tT  - %4$s  -  %2$s  -  %5$s%6$s%n");

        Logger rootLog = Logger.getLogger("");
        rootLog.setLevel(Level.FINE);
        rootLog.getHandlers()[0].setLevel(Level.FINE);

        ServerGui gui = new DefaultCliServerGui();
        System.out.println("   __ __ ______ ______ ___    ____                         \n"
                + "  / // //_  __//_  __// _ \\  / __/___  ____ _  __ ___  ____\n"
                + " / _  /  / /    / /  / ___/ _\\ \\ / -_)/ __/| |/ // -_)/ __/\n"
                + "/_//_/  /_/    /_/  /_/    /___/ \\__//_/   |___/ \\__//_/   \n");
        System.out.println("https://github.com/piotrpolak/android-http-server");
        System.out.println("");
        final ControllerImpl controllerImpl = new ControllerImpl(getServerConfigFactory(),
                ServerSocketFactory.getDefault(),
                gui);
        controllerImpl.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                controllerImpl.stop();
            }
        });
    }

    /**
     * Creates and returns an instance of ServerConfigFactory.
     * If you want to provide your own ServerConfig factory, this is the place to overwrite.
     *
     * @return
     */
    protected ServerConfigFactory getServerConfigFactory() {
        return new DefaultServerConfigFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("The server has stopped.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        LOGGER.info("The server has started.");
    }
}
