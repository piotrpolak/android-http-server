package ro.polak.http.utilities;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class IOUtilitiesTest {

    @Test
    public void shouldCloseClosableSilently() throws IOException {
        Closeable closeable = mock(Closeable.class);
        doThrow(new IOException("This should never happen")).when(closeable).close();

        try {
            closeable.close();
            fail("IOException should be thrown.");
        } catch (Exception e) {
        }

        IOUtilities.closeSilently(closeable);
    }
}