package ro.polak.http.errorhandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.FileUtils;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public final  class AbstractHtmlErrorHandlerTest {

    private static OutputStream outputStream;
    private static HttpServletResponseImpl response;

    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        response = new HttpServletResponseImpl(
                mock(Serializer.class),
                mock(Serializer.class),
                new StreamHelper(new RangeHelper(), new RangePartHeaderSerializer()),
                outputStream);
    }

    @Test
    public void shouldThrowExceptionWhenDocumentPathIsMissing() throws Exception {
        final AbstractHtmlErrorHandler handler
                = new SampleHtmlErrorHanlder("500", "", "", "/tmp/nonexistend");
        assertThrows(IOException.class, new Executable() {
            @Override
            public void execute() throws IOException {
                handler.serve(response);
            }
        });
    }

    @Test
    public void shouldServeBuiltinDocument() throws Exception {
        AbstractHtmlErrorHandler handler
                = new SampleHtmlErrorHanlder("500", "MSG_TOKEN", "EXPLANATION_TOKEN", null);
        handler.serve(response);

        assertThat(response.getStatus(), containsString("500"));
        assertThat(outputStream.toString(), containsString("MSG_TOKEN"));
        assertThat(outputStream.toString(), containsString("EXPLANATION_TOKEN"));
    }

    @Test
    public void shouldServeExternalDocument() throws IOException {
        File file = FileUtils.writeToTempFile("FILE_TOKEN");

        AbstractHtmlErrorHandler handler
                = new SampleHtmlErrorHanlder("", "", "", file.getAbsolutePath());
        handler.serve(response);
        assertThat(outputStream.toString(), containsString("FILE_TOKEN"));
    }

    private static class SampleHtmlErrorHanlder extends AbstractHtmlErrorHandler {
        SampleHtmlErrorHanlder(final String status, final String message, final String explanation,
                               final String errorDocumentPath) {
            super(status, message, explanation, errorDocumentPath);
        }
    }
}
// CHECKSTYLE.ON: JavadocType
