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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import ro.polak.webserver.servlet.HttpSessionWrapper;

/**
 * Filesystem session storage.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class FileSessionStorage implements SessionStorage {

    private static final Logger LOGGER = Logger.getLogger(FileSessionStorage.class.getName());

    private String tempPath;
    private static Pattern pattern = Pattern.compile("[a-z]+");

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
        if (session.getId() == null || session.getId().equals("")) {
            throw new IllegalArgumentException("Session ID can not be empty");
        }

        File file = new File(getSessionStoragePath(session.getId()));
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(session);

        LOGGER.log(Level.FINE, "Persisted session {0} in {1}",
                new Object[]{session.getId(), tempPath});

        try {
            out.close();
        } catch (IOException e) {
        }
    }

    @Override
    public HttpSessionWrapper getSession(String id) throws IOException {
        HttpSessionWrapper session = null;
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
                    LOGGER.log(Level.WARNING,
                            "Unable to read session " + id + " under " + tempPath, e);
                }
            } else {
                LOGGER.log(Level.FINE, "Session file does not exist {0} under {1}",
                        new Object[]{id, tempPath});
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
