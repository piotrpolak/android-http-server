package ro.polak.http.impl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import ro.polak.http.MimeTypeMapping;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class MimeTypeMappingImplTest {

    @Test
    public void shouldSuportMultivaluedLine() throws IOException {
        String input = "image/jpeg jpeg jpg jpe";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mimeTypeMapping = MimeTypeMappingImpl.createFromStream(stream);
        assertThat(mimeTypeMapping.getMimeTypeByExtension("jpg"), is("image/jpeg"));
        assertThat(mimeTypeMapping.getMimeTypeByExtension("jpeg"), is("image/jpeg"));
        assertThat(mimeTypeMapping.getMimeTypeByExtension("jpe"), is("image/jpeg"));
        stream.close();
    }

    @Test
    public void shouldNormalizeLetterCase() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mimeTypeMapping = MimeTypeMappingImpl.createFromStream(stream);
        assertThat(mimeTypeMapping.getMimeTypeByExtension("Jpg"), is("image/jpeg"));
        assertThat(mimeTypeMapping.getMimeTypeByExtension("Jpeg"), is("image/jpeg"));
        assertThat(mimeTypeMapping.getMimeTypeByExtension("Jpe"), is("image/jpeg"));
        stream.close();
    }

    @Test
    public void shouldReturnDefaultMimeType() throws IOException {
        String input = "image/jpeg jPEG jPG jPE";
        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        MimeTypeMapping mimeTypeMapping = MimeTypeMappingImpl.createFromStream(stream, "default/default");
        assertThat(mimeTypeMapping.getMimeTypeByExtension("any"), is("default/default"));
        assertThat(mimeTypeMapping.getMimeTypeByExtension(null), is("default/default"));
        stream.close();
    }
}
// CHECKSTYLE.ON: JavadocType
