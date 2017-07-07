package ro.polak.http.session.storage;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import ro.polak.http.servlet.HttpSessionWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;

public class FileSessionStorageTest {

    private static final String VALID_SESSION_ID = "sessionidsjdfhgskldjfsghldkfjsgg";
    private static final String ILLEGAL_SESSION_ID = "////////////////////////////////";
    private static FileSessionStorage fileSessionStorage;
    private static String tempPath = System.getProperty("java.io.tmpdir") + "/";

    @BeforeClass
    public static void setUp() {
        fileSessionStorage = new FileSessionStorage(tempPath);
    }

    @Test
    public void shouldPersistRestoreAndRemoveSession() throws IOException {
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(VALID_SESSION_ID);
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
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(VALID_SESSION_ID);
        sessionWrapper.setAttribute("attributeName", "SomeValue");
        fileSessionStorage.persistSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(not(nullValue())));
        assertThat((String) sessionWrapper.getAttribute("attributeName"), is("SomeValue"));

        HttpSessionWrapper session2Wrapper = new HttpSessionWrapper(VALID_SESSION_ID);
        session2Wrapper.setAttribute("otherName", "OtherValue");
        fileSessionStorage.persistSession(session2Wrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(not(nullValue())));
        assertThat((String) sessionWrapper.getAttribute("attributeName"), is(nullValue()));
        assertThat((String) sessionWrapper.getAttribute("otherName"), is("OtherValue"));

        fileSessionStorage.removeSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(VALID_SESSION_ID);
        assertThat(sessionWrapper, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionNameLength() throws IOException {
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper("abcX8");
        fileSessionStorage.persistSession(sessionWrapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionNameNull() throws IOException {
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(null);
        fileSessionStorage.persistSession(sessionWrapper);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionNameIllegalCharacters() throws IOException {
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(ILLEGAL_SESSION_ID);
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
        File sessionFile = new File(tempPath + sid + "_session");
        if (sessionFile.exists()) {
            sessionFile.delete();
        }
        sessionFile.createNewFile();
        assertThat(fileSessionStorage.getSession(sid), is(nullValue()));
    }
}