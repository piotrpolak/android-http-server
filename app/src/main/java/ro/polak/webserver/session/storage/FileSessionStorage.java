/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.session.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HttpSessionWrapper;

/**
 * Filesystem session storage.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class FileSessionStorage implements SessionStorage {

    private String tempPath;

    /**
     * Default constructor.
     *
     * @param tempPath
     */
    public FileSessionStorage(String tempPath) {
        this.tempPath = tempPath;
    }

    @Override
    public void persistSession(HttpSessionWrapper session) throws IOException {
        if (session.getId() == null || session.getId() == "") {
            throw new IllegalArgumentException("Session ID can not be empty");
        }

        File file = new File(getSessionStoragePath(session.getId()));
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(session);

        MainController.getInstance().println(getClass(), "Persisted session: " + tempPath + session.getId());

        try {
            out.close();
        } catch (IOException e) {
        }
    }

    @Override
    public HttpSessionWrapper getSession(String id) throws IOException {
        HttpSessionWrapper session = null;

        Pattern pattern = Pattern.compile("[a-z]+");
        boolean isIdValid = id != null && id.length() == 32 && pattern.matcher(id).matches();
        if (isIdValid) {
            File file = new File(getSessionStoragePath(id));

            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fis);
                    session = (HttpSessionWrapper) in.readObject();

                    try {
                        in.close();
                    } catch (IOException e) {
                    }

                } catch (ClassNotFoundException e) {
                } catch (IOException e) {
                    MainController.getInstance().println(getClass(), "Unable to read session: " + tempPath + id);
                }
            } else {
                MainController.getInstance().println(getClass(), "Session file does not exist: " + tempPath + id);
            }
        }

        return session;
    }

    @Override
    public boolean removeSession(HttpSessionWrapper session) {
        File file = new File(getSessionStoragePath(session.getId()));
        return file.delete();
    }

    private String getSessionStoragePath(String id) {
        return tempPath + id + "_session";
    }
}
