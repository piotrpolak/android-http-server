/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ro.polak.http.ServerConfig;
import ro.polak.http.servlet.HttpSession;
import ro.polak.http.utilities.ConfigReader;

import static ro.polak.http.utilities.IOUtilities.closeSilently;

public class AccessControl {

    private static final Logger LOGGER = Logger.getLogger(AccessControl.class.getName());

    private ServerConfig serverConfig;
    private HttpSession session;


    /**
     * Default constructor
     *
     * @param serverConfig
     * @param session
     */
    public AccessControl(ServerConfig serverConfig, HttpSession session) {
        this.serverConfig = serverConfig;
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
            LOGGER.fine("No session, not logged in");
            return false;
        }

        if (session.getAttribute("loggedin") != null) {
            // There must be an attribute loggedin and it must be equal 1
            if (session.getAttribute("loggedin").equals("1")) {
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
            Map<String, String> config = getConfig(serverConfig);
            if (config.get("_managementLogin").equals(login) && config.get("_managementPassword").equals(password)) {
                session.setAttribute("loggedin", "1");
                logged = true;
            } else {
                LOGGER.fine("Not logging in - wrong password");
            }
        } catch (IOException e) {
            LOGGER.fine("Not logging in - IOException " + e.getMessage());
        }
        return logged;
    }

    /**
     * Returns server config
     *
     * @param serverConfig
     * @return
     */
    public static Map<String, String> getConfig(ServerConfig serverConfig) throws IOException {
        Map<String, String> config = null;
        InputStream fileInputStream = null;

        File basePath = new File(serverConfig.getBasePath());
        if (!basePath.exists()) {
            if (!basePath.mkdir()) {
                throw new IOException("Unable to mkdir " + basePath.getAbsolutePath());
            }
        }

        try {
            ConfigReader reader = new ConfigReader();
            String configPath = serverConfig.getBasePath() + "admin.conf";
            fileInputStream = new FileInputStream(configPath);
            config = reader.read(fileInputStream);
        } finally {
            if (fileInputStream != null) {
                closeSilently(fileInputStream);
            }
            if (config == null) {
                LOGGER.fine("Creating a default config");
                config = new HashMap<>();
            }
        }
        return config;
    }
}
