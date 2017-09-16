package ro.polak.http.servlet;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassPathServletPathTranslatorTest {

    private ClassPathServletPathTranslator translator = new ClassPathServletPathTranslator();

    @Test
    public void shouldTranslatePath() {
        assertThat(translator.toClassName("/example/path"), is("example.path"));
        assertThat(translator.toClassName("/example/path/"), is("example.path."));
    }

    @Test
    public void shouldTranslatePathAndRemoveExtension() {
        assertThat(translator.toClassName("/example/path.dhtml"), is("example.path"));
        assertThat(translator.toClassName("/example/path."), is("example.path"));
    }

    @Test
    public void shouldTranslateCaseSensitivePath() {
        assertThat(translator.toClassName("/Example/Path"), is("Example.Path"));
    }
}