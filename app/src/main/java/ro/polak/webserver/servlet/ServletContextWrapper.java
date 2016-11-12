/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.utilities.RandomStringGenerator;
import ro.polak.utilities.Utilities;
import ro.polak.webserver.ServerConfig;
import ro.polak.webserver.session.storage.SessionStorage;

/**
 * Servlet context implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServletContextWrapper implements ServletContext {

    private static final Logger LOGGER = Logger.getLogger(ServletContextWrapper.class.getName());

    private ServerConfig serverConfig;
    private SessionStorage sessionStorage;

    /**
     * Default constructor.
     *
     * @param serverConfig
     * @param sessionStorage
     */
    public ServletContextWrapper(ServerConfig serverConfig, SessionStorage sessionStorage) {
        this.serverConfig = serverConfig;
        this.sessionStorage = sessionStorage;
    }

    @Override
    public String getMimeType(String file) {
        return serverConfig.getMimeTypeMapping().
                getMimeTypeByExtension(Utilities.getExtension(file));
    }

    /**
     * Gets session for the given id.
     *
     * @param id
     * @return
     */
    public HttpSessionWrapper getSession(String id) {
        HttpSessionWrapper session = null;

        try {
            session = sessionStorage.getSession(id);
            if (session != null) {
                session.setServletContext(this);

                if (isSessionExpired(session)) {
                    sessionStorage.removeSession(session);
                    LOGGER.log(Level.FINE, "Removed expired session {0}",
                            new Object[]{session.getId()});
                    session = null;
                }
            }

        } catch (IOException e) {
        }

        return session;
    }

    /**
     * Creates a new session.
     *
     * @return
     */
    public HttpSessionWrapper createNewSession() {
        HttpSessionWrapper session = new HttpSessionWrapper(RandomStringGenerator.generate());
        session.setServletContext(this);
        LOGGER.log(Level.FINE, "Created a new session {0}",
                new Object[]{session.getId()});
        return session;
    }

    /**
     * Handles session storage/invalidation, sets session cookies.
     *
     * @param session
     * @param response
     * @throws IOException
     */
    public void handleSession(HttpSessionWrapper session, HttpResponseWrapper response) throws IOException {
        Cookie cookie = new Cookie(HttpSessionWrapper.COOKIE_NAME, "");
        if (session.isInvalidated()) {
            cookie.setMaxAge(-100);

            sessionStorage.removeSession(session);
            LOGGER.log(Level.FINE, "Invalidated session {0}",
                    new Object[]{session.getId()});
        } else {
            cookie.setValue(session.getId());
            sessionStorage.persistSession(session);
        }

        response.addCookie(cookie);
    }

    private boolean isSessionExpired(HttpSessionWrapper session) {
        return System.currentTimeMillis() - session.getMaxInactiveInterval() * 1000 > session.getLastAccessedTime();
    }
}
