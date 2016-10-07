package ro.polak.webserver;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MimeTypeMappingTest {

    @Test
    public void shouldSuportMultivaluedLine() throws IOException {
        String input = "image/jpeg jpeg jpg jpe";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mapping = new MimeTypeMapping(stream);
        assertThat(mapping.getMimeTypeByExtension("jpg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("jpeg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("jpe"), is("image/jpeg"));
    }

    @Test
    public void shouldNormalizeLetterCase() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mapping = new MimeTypeMapping(stream);
        assertThat(mapping.getMimeTypeByExtension("Jpg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("Jpeg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("Jpe"), is("image/jpeg"));
    }

    @Test
    public void shouldReturnDefaultMimeType() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mapping = new MimeTypeMapping(stream, "default/default");
        assertThat(mapping.getMimeTypeByExtension("any"), is("default/default"));
        assertThat(mapping.getMimeTypeByExtension(null), is("default/default"));
    }
}