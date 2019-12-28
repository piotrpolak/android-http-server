package ro.polak.http.session.storage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ro.polak.http.FileUtils;
import ro.polak.http.OsUtils;
import ro.polak.http.servlet.impl.HttpSessionImpl;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assume.assumeFalse;

// CHECKSTYLE.OFF: JavadocType
public class FileSessionStorageTest {

    private static final String VALID_SESSION_ID = "sessionidsjdfhgskldjfsghldkfjsgg";
    private static final String ILLEGAL_SESSION_ID = "////////////////////////////////";
    private static FileSessionStorage fileSessionStorage;
    private static String workingDirectory;

    @BeforeClass
    public static void setUp() throws IOException {
        workingDirectory = FileUtils.createTempDirectory();
        fileSessionStorage = new FileSessionStorage(workingDirectory);
    }

    @AfterClass
    public static void cleanUp() {
        new File(workingDirectory).delete();
    }

    @Test
    public void shouldPersistRestoreAndRemoveSession() throws IOException {
        HttpSessionImpl sessionWrapper = new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis());
        sessionWrapper.setAttribute("attributeName", "SomeValue");
        fileSessionStorage.persistSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(not(nullValue())));
        assertThat((String) sessionWrapper.getAttribute("attributeName"), is("SomeValue"));

        fileSessionStorage.removeSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(nullValue()));
    }

    @Test
    public void shouldPersistSessionAndOverWriteFile() throws IOException {
        HttpSessionImpl sessionWrapper = new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis());
        sessionWrapper.setAttribute("attributeName", "SomeValue");
        fileSessionStorage.persistSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(not(nullValue())));
        assertThat((String) sessionWrapper.getAttribute("attributeName"), is("SomeValue"));

        HttpSessionImpl session2Wrapper = new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis());
        session2Wrapper.setAttribute("otherName", "OtherValue");
        fileSessionStorage.persistSession(session2Wrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(not(nullValue())));
        assertThat(sessionWrapper.getAttribute("attributeName"), is(nullValue()));
        assertThat((String) sessionWrapper.getAttribute("otherName"), is("OtherValue"));

        fileSessionStorage.removeSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionNameLength() throws IOException {
        HttpSessionImpl sessionWrapper = new HttpSessionImpl("abcX8", System.currentTimeMillis());
        fileSessionStorage.persistSession(sessionWrapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionNameNull() throws IOException {
        HttpSessionImpl sessionWrapper = new HttpSessionImpl(null, System.currentTimeMillis());
        fileSessionStorage.persistSession(sessionWrapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionNameIllegalCharacters() throws IOException {
        HttpSessionImpl sessionWrapper = new HttpSessionImpl(ILLEGAL_SESSION_ID, System.currentTimeMillis());
        fileSessionStorage.persistSession(sessionWrapper);
    }

    @Test
    public void shouldReturnNullOnInvalidSessionName() throws IOException {
        assertThat(fileSessionStorage.getSession(null), is(nullValue()));
        assertThat(fileSessionStorage.getSession("abcX8"), is(nullValue()));
        assertThat(fileSessionStorage.getSession("/asdfghjklzxasdfghjklzxasdfghjklzxz"), is(nullValue()));
    }

    @Test
    public void shouldFailSilentlyOnInvalidFileContents() throws IOException {
        String sid = "asdfghjklzxasdfghjklzxasdfghjklz";
        File sessionFile = new File(workingDirectory + sid + "_session");
        if (sessionFile.exists() && !sessionFile.delete()) {
            throw new IOException("Unable to delete file " + sessionFile.getAbsolutePath());
        }
        if (!sessionFile.createNewFile()) {
            throw new IOException("Unable to create new file " + sessionFile.getAbsolutePath());
        }
        assertThat(fileSessionStorage.getSession(sid), is(nullValue()));
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenUnableToCreateSessionDirectory() throws IOException {
        assumeFalse(OsUtils.isWindows());

        String nonWritableDirectory = FileUtils.createTempDirectory();
        new File(nonWritableDirectory).setWritable(false);

        FileSessionStorage storage = new FileSessionStorage(nonWritableDirectory);
        HttpSessionImpl sessionWrapper = new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis());
        sessionWrapper.setAttribute("attributeName", "SomeValue");
        storage.persistSession(sessionWrapper);
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenUnableToCreateFile() throws IOException {
        String nonExistentDirectory = "/tmp/nonexistent-" + Math.random() + "/";
        assertThat(new File(nonExistentDirectory).exists(), is(false));
        SessionStorage sessionStorage = new FileSessionStorage(nonExistentDirectory);
        sessionStorage.persistSession(new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis()));
    }
}
// CHECKSTYLE.ON: JavadocType
