/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin.logic;

import java.util.logging.Logger;

import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.servlet.HttpSession;

/**
 * Access control support class.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class AccessControl {

    private static final Logger LOGGER = Logger.getLogger(AccessControl.class.getName());
    public static final String ATTR_LOGGEDIN = "loggedin";

    private ServerConfig serverConfig;
    private HttpSession session;


    /**
     * Default constructor.
     *
     * @param serverConfig
     * @param session
     */
    public AccessControl(final ServerConfig serverConfig, final HttpSession session) {
        this.serverConfig = serverConfig;
        this.session = session;
    }

    /**
     * Tells whether the user is logged.
     *
     * @return
     */
    public boolean isLogged() {
        if (session == null) {
            LOGGER.fine("No session, not logged in");
            return false;
        }

        if (session.getAttribute(ATTR_LOGGEDIN) != null) {
            if (session.getAttribute(ATTR_LOGGEDIN).equals("1")) {
                return true;
            } else {
                LOGGER.fine("Not logging in - session attribute is NOT null");
            }
        } else {
            LOGGER.fine("Not logging in - session attribute is null");
        }

        return false;
    }

    /**
     * Logs off the currently logged user.
     */
    public void logout() {
        session.setAttribute(ATTR_LOGGEDIN, null);
    }

    /**
     * Logs the user in if the login and password match.
     *
     * @param login
     * @param password
     * @return
     */
    public boolean doLogin(final String login, final String password) {
        if (serverConfig.getAttribute("admin.login").equals(login)
                && serverConfig.getAttribute("admin.password").equals(password)) {
            session.setAttribute(ATTR_LOGGEDIN, "1");
            return true;
        } else {
            LOGGER.fine("Not logging in - wrong password");
        }
        return false;
    }
}
