package ro.polak.http.session.storage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.io.IOException;

import ro.polak.http.FileUtils;
import ro.polak.http.OsUtils;
import ro.polak.http.servlet.impl.HttpSessionImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

// CHECKSTYLE.OFF: JavadocType
public class FileSessionStorageTest {

    private static final String VALID_SESSION_ID = "sessionidsjdfhgskldjfsghldkfjsgg";
    private static final String ILLEGAL_SESSION_ID = "////////////////////////////////";
    private static FileSessionStorage fileSessionStorage;
    private static String workingDirectory;

    @BeforeAll
    public static void setUp() throws IOException {
        workingDirectory = FileUtils.createTempDirectory();
        fileSessionStorage = new FileSessionStorage(workingDirectory);
    }

    @AfterAll
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

    @Test
    public void shouldValidateSessionNameLength() {
        final HttpSessionImpl sessionWrapper = new HttpSessionImpl("abcX8", System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                fileSessionStorage.persistSession(sessionWrapper);
            }
        });
    }

    @Test
    public void shouldValidateSessionNameNull() {
        final HttpSessionImpl sessionWrapper = new HttpSessionImpl(null, System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                fileSessionStorage.persistSession(sessionWrapper);
            }
        });

    }

    @Test
    public void shouldValidateSessionNameIllegalCharacters() {
        final HttpSessionImpl sessionWrapper = new HttpSessionImpl(ILLEGAL_SESSION_ID, System.currentTimeMillis());
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                fileSessionStorage.persistSession(sessionWrapper);
            }
        });
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

    @Test
    public void shouldThrowExceptionWhenUnableToCreateSessionDirectory() throws IOException {
        assumeFalse(OsUtils.isWindows());

        String nonWritableDirectory = FileUtils.createTempDirectory();
        new File(nonWritableDirectory).setWritable(false);

        final FileSessionStorage storage = new FileSessionStorage(nonWritableDirectory);
        final HttpSessionImpl sessionWrapper = new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis());
        sessionWrapper.setAttribute("attributeName", "SomeValue");
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                storage.persistSession(sessionWrapper);
            }
        });
    }

    @Test
    public void shouldThrowExceptionWhenUnableToCreateFile() {
        String nonExistentDirectory = "/tmp/nonexistent-" + Math.random() + "/";
        assertThat(new File(nonExistentDirectory).exists(), is(false));
        final SessionStorage sessionStorage = new FileSessionStorage(nonExistentDirectory);
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                sessionStorage.persistSession(new HttpSessionImpl(VALID_SESSION_ID, System.currentTimeMillis()));
            }
        });
    }
}
// CHECKSTYLE.ON: JavadocType
