/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.session.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import ro.polak.http.servlet.HttpSessionWrapper;
import ro.polak.http.utilities.IOUtilities;

/**
 * Filesystem session storage.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class FileSessionStorage implements SessionStorage {

    private static final Logger LOGGER = Logger.getLogger(FileSessionStorage.class.getName());
    public static final String SESSION_FILE_SUFFIX = "_session";
    private static final Pattern pattern = Pattern.compile("[a-z]+");

    private final String tempPath;

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
        if (!isSessionIdValid(session.getId())) {
            throw new IllegalArgumentException("Session ID can not be empty and must be composed of 32 characters");
        }

        File file = new File(getSessionStoragePath(session.getId()));
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Unable to create new file " + file.getAbsolutePath());
        }
        writeSession(session, file);

        LOGGER.log(Level.FINE, "Persisted session {0} in {1}",
                new Object[]{session.getId(), tempPath});
    }

    @Override
    public HttpSessionWrapper getSession(String id) throws IOException {
        HttpSessionWrapper session = null;
        if (isSessionIdValid(id)) {
            File file = new File(getSessionStoragePath(id));

            if (file.exists()) {
                session = readSession(id, file);
            } else {
                LOGGER.log(Level.FINE, "Session file does not exist {0} under {1}",
                        new Object[]{id, tempPath});
            }
        }

        return session;
    }

    /**
     * Session ID must be verified both on reading and writing in order to prevent from a potential
     * file system damage in case Session ID is a valid file path.
     *
     * @param id
     * @return
     */
    private boolean isSessionIdValid(String id) {
        return id != null && id.length() == 32 && pattern.matcher(id).matches();
    }

    @Override
    public boolean removeSession(HttpSessionWrapper session) {
        File file = new File(getSessionStoragePath(session.getId()));
        return file.delete();
    }

    private HttpSessionWrapper readSession(String id, File file) {
        HttpSessionWrapper session = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            session = (HttpSessionWrapper) objectInputStream.readObject();

            IOUtilities.closeSilently(objectInputStream);
            IOUtilities.closeSilently(fileInputStream);

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Unable to read session " + id + " under " + tempPath, e);
        }
        return session;
    }

    private void writeSession(HttpSessionWrapper session, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(session);

        IOUtilities.closeSilently(objectOutputStream);
        IOUtilities.closeSilently(fileOutputStream);
    }

    private String getSessionStoragePath(String id) {
        return tempPath + id + SESSION_FILE_SUFFIX;
    }
}
