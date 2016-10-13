/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import ro.polak.utilities.Config;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HttpSessionWrapper;

public class AccessControl {

    protected HttpSessionWrapper session;
    private static Config config;

    /**
     * Default constructor
     *
     * @param session
     */
    public AccessControl(HttpSessionWrapper session) {
        this.session = session;
    }

    /**
     * Tells whether the user is logged
     *
     * @return
     */
    public boolean isLogged() {
        // There is no session active
        if (session == null) {
            MainController.getInstance().println(getClass(), "No session, not logged in");
            return false;
        }

        if (session.getAttribute("loggedin") != null) {
            // There must be an attribute loggedin and it must be equal 1
            if (session.getAttribute("loggedin").equals("1")) {
                return true;
            } else {
                MainController.getInstance().println(getClass(), "Not logging in - session attribute is NOT null");
            }
        } else {
            MainController.getInstance().println(getClass(), "Not logging in - session attribute is null");
        }


        return false;
    }

    /**
     * Logs off the currently logged user
     */
    public void logout() {
        session.setAttribute("loggedin", null);
    }

    /**
     * Logs the user in if the login and password match
     *
     * @param login
     * @param password
     * @return
     */
    public boolean doLogin(String login, String password) {
        boolean logged = false;
        try {
            // TODO Get the user from storage
            if (AccessControl.getConfig().get("_managementLogin").equals(login) && AccessControl.getConfig().get("_managementPassword").equals(password)) {
                session.setAttribute("loggedin", "1");
                logged = true;
            }
        } catch (NullPointerException e) {
            logged = false;
        }
        return logged;
    }

    /**
     * Returns server config
     *
     * @return
     */
    public static Config getConfig() {
        // Initializes config only once
        if (config == null) {
            config = new Config();
            config.read(MainController.getInstance().getWebServer().getServerConfig().getBasePath() + "admin.conf");
        }

        return config;
    }
}
