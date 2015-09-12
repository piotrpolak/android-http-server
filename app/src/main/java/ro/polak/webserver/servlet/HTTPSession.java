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
import ro.polak.webserver.JLWSConfig;

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
    private String directoryPath = JLWSConfig.TempDir;
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
            vars = new Hashtable<String,String>();
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
        if (!isStarted) {
            this.start();
        }
        vars.put(varName, varValue);
    }

    /**
     * Gets session attribute of the specified name
     *
     * @param varName Attribute name
     * @return Attribute value
     */
    public String getAttribute(String varName) {
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
    public String getID() {
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

    protected void freeze() {
        if (isStarted == false) {
            return;
        }

        File file = new File(directoryPath + sid);
        try {
            file.createNewFile();
        } catch (Exception e) {
            // Unable to create session file
        }

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            out.writeObject(vars);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    protected boolean unfreeze() {
        if (isStarted == true) {
            return false;
        }

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(new File(directoryPath + sid));
            in = new ObjectInputStream(fis);
            vars = (Hashtable) in.readObject();
            in.close();
            return true;
        } catch (IOException e) {
            sid = RandomStringGenerator.generate();
            response.setCookie(cookieName, sid);
            vars = new Hashtable<String,String>();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
