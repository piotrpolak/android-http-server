/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.regex.Pattern;
import java.util.Hashtable;

import ro.polak.utilities.RandomStringGenerator;
import ro.polak.webserver.controller.MainController;

/**
 * Session mechanism for little servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class HTTPSession {

    public static final Pattern pattern = Pattern.compile("[a-z]+");
    private transient HTTPRequest request;
    private transient HTTPResponse response;
    private String sid;
    private String directoryPath = MainController.getInstance().getServer().getServerConfig().getTempPath();
    private String cookieName = "JSSSESSIONID";
    private Hashtable vars;
    private boolean isStarted = false;

    /**
     * Session constructor
     *
     * @param request  http request
     * @param response http response
     */
    public HTTPSession(HTTPRequest request, HTTPResponse response) {
        this.response = response;
        this.request = request;
    }

    /**
     * Initializes session, makes the session variables usable.
     * <p/>
     * Unfreezes or starts a new session
     */
    private void start() {
        if (isStarted == true) {
            return;
        }

        // Checks for cookie
        sid = request.getCookie(cookieName);
        boolean sessionUnfreezed = false;

        // Adds protection
        if (sid != null && sid.length() == 32 && pattern.matcher(sid).matches()) {
            sessionUnfreezed = unfreeze();
        }

        if (!sessionUnfreezed) {
            sid = RandomStringGenerator.generate();
            response.setCookie(cookieName, sid);
            vars = new Hashtable<String, String>();
        }

        isStarted = true;
    }

    /**
     * Sets session attribute
     *
     * @param varName  attribute name
     * @param varValue attribute value
     */
    public void setAttribute(String varName, String varValue) {
        // Lazy load
        if (!isStarted) {
            this.start();
        }

        if (varValue == null) {
            // Removing the attribute when the value is null
            vars.remove(varName);
        } else {
            vars.put(varName, varValue);
        }
    }

    /**
     * Gets session attribute of the specified name
     *
     * @param varName Attribute name
     * @return Attribute value
     */
    public String getAttribute(String varName) {
        // Lazy load
        if (!isStarted) {
            this.start();
        }

        try {
            return (String) vars.get(varName);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns session's id
     *
     * @return session's id
     */
    public String getId() {
        return this.sid;
    }

    /**
     * Destroys session and frees resources
     */
    public void destroy() {
        // For freeze method
        isStarted = false;

        // Remove cookie
        response.setCookie(cookieName, "", -100);

        // Delete file
        File file = new File(directoryPath + sid);
        try {
            file.delete();
        } catch (Exception e) {
        }
    }

    /**
     * Persists the session to the storage
     */
    protected void freeze() {
        // Prevent from saving an empty session
        if (isStarted == false) {
            return;
        }

        File file = new File(directoryPath + sid);
        try {
            file.createNewFile();
        } catch (Exception e) {
            // Unable to create session file
        }

        // Writing session object to the file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(vars);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Restores session from the storage
     *
     * @return
     */
    protected boolean unfreeze() {
        // Prevent from reading an empty session
        if (isStarted == true) {
            return false;
        }

        // Reading session object to the file
        try {
            // TODO Check if the file exists
            FileInputStream fis = new FileInputStream(new File(directoryPath + sid));
            ObjectInputStream in = new ObjectInputStream(fis);
            vars = (Hashtable) in.readObject();
            in.close();
            return true;
        } catch (IOException e) {
            // TODO Check if the file exists, generate session only if the file is missing
            sid = RandomStringGenerator.generate();
            response.setCookie(cookieName, sid);
            vars = new Hashtable<String, String>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
