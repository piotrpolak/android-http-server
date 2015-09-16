package admin;

import ro.polak.utilities.Config;
import ro.polak.webserver.controller.MainController;

public class AccessControl {

    protected ro.polak.webserver.servlet.HTTPSession session;

    public AccessControl(ro.polak.webserver.servlet.HTTPSession session) {
        this.session = session;
    }

    public boolean isLogged() {
        if (session == null) {
            return false;
        }
        if (session.getAttribute("loggedin") == null) {
            return false;
        }
        if (session.getAttribute("loggedin").equals("1")) {
            return true;
        }
        return false;
    }

    public void logout() {
        session.setAttribute("loggedin", null);
    }

    public boolean doLogin(String login, String password) {
        boolean logged = false;
        try {
            if (AccessControl.getConfig().get("_managementLogin").equals(login) && AccessControl.getConfig().get("_managementPassword").equals(password)) {

                session.setAttribute("loggedin", "1");
                logged = true;

                // Get user from DB
            }
        } catch (NullPointerException e) {
            // e.printStackTrace();
            logged = false;
        }
        return logged;
    }

    public static Config getConfig() {
        Config config = new Config();
        config.read(MainController.getInstance().getServer().getServerConfig().getBasePath() + "admin.conf");

        return config;
    }
}
