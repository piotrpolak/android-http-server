package ro.polak.http.session.storage;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import ro.polak.http.servlet.HttpSessionWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;

public class FileSessionStorageTest {

    private static final String sessionId = "sessionidsjdfhgskldjfsghldkfjsgg";
    private static FileSessionStorage fileSessionStorage;

    @BeforeClass
    public static void setup() {
        fileSessionStorage = new FileSessionStorage("/tmp/webserver/");
    }

    @Test
    public void shouldPersistRestoreAndRemoveSession() throws IOException {
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper(sessionId);
        sessionWrapper.setAttribute("attributeName", "SomeValue");
        fileSessionStorage.persistSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(sessionId);
        assertThat(sessionWrapper, is(not(nullValue())));
        assertThat((String) sessionWrapper.getAttribute("attributeName"), is("SomeValue"));

        fileSessionStorage.removeSession(sessionWrapper);

        sessionWrapper = fileSessionStorage.getSession(sessionId);
        assertThat(sessionWrapper, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldValidateSessionName() throws IOException {
        HttpSessionWrapper sessionWrapper = new HttpSessionWrapper("abcX8");
        fileSessionStorage.persistSession(sessionWrapper);
    }
}