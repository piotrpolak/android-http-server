package ro.polak.webserver.impl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import ro.polak.webserver.MimeTypeMapping;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MimeTypeMappingImplTest {

    @Test
    public void shouldSuportMultivaluedLine() throws IOException {
        String input = "image/jpeg jpeg jpg jpe";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mapping = MimeTypeMappingImpl.createFromStream(stream);
        assertThat(mapping.getMimeTypeByExtension("jpg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("jpeg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("jpe"), is("image/jpeg"));
    }

    @Test
    public void shouldNormalizeLetterCase() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mapping = MimeTypeMappingImpl.createFromStream(stream);
        assertThat(mapping.getMimeTypeByExtension("Jpg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("Jpeg"), is("image/jpeg"));
        assertThat(mapping.getMimeTypeByExtension("Jpe"), is("image/jpeg"));
    }

    @Test
    public void shouldReturnDefaultMimeType() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mapping = MimeTypeMappingImpl.createFromStream(stream, "default/default");
        assertThat(mapping.getMimeTypeByExtension("any"), is("default/default"));
        assertThat(mapping.getMimeTypeByExtension(null), is("default/default"));
    }
}