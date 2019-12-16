package ro.polak.http.utilities;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static ro.polak.http.ExtraMarchers.utilityClass;

// CHECKSTYLE.OFF: JavadocType
public class IOUtilitiesTest {

    @Test
    public void shouldNotBeInstantiableFinalClass() {
        assertThat(IOUtilities.class, is(utilityClass()));
    }

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

    @Test
    public void shouldCloseNullClosableSilently() {
        try {
            IOUtilities.closeSilently(null);
        } catch (Exception e) {
            fail("Exception should not be thrown.");
        }
    }

    @Test
    public void shouldCopyStreams() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtilities.copyStreams(in, out);
        assertThat(out.toString(), is(input));
    }
}
// CHECKSTYLE.ON: JavadocType
