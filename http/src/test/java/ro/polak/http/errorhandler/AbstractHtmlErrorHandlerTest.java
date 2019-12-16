package ro.polak.http.errorhandler;

import org.junit.Before;
import org.junit.Test;
import ro.polak.http.FileUtils;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public class AbstractHtmlErrorHandlerTest {

    private static OutputStream outputStream;
    private static HttpServletResponseImpl response;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        response = new HttpServletResponseImpl(
                mock(Serializer.class),
                mock(Serializer.class),
                new StreamHelper(new RangeHelper(), new RangePartHeaderSerializer()),
                outputStream);
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenDocumentPathIsMissing() throws Exception {
        AbstractHtmlErrorHandler handler
                = new SampleHtmlErrorHanlder("500", "", "", "/tmp/nonexistend");
        handler.serve(response);
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
