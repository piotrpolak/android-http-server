package ro.polak.http.errorhandler;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.StreamHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;

public class AbstractHtmlErrorHandlerTest {

    @Test(expected = IOException.class)
    public void shouldThrowExceptionWhenDocumentPathIsMissing() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        AbstractHtmlErrorHandler handler
                = new SampleHtmlErrorHanlder("500", "", "", "/tmp/nonexistend");
        handler.serve(response);
    }

    @Test
    public void shouldServeBuiltinDocument() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponseWrapper response = new HttpResponseWrapper(
                mock(Serializer.class),
                mock(Serializer.class),
                mock(StreamHelper.class),
                outputStream);
        AbstractHtmlErrorHandler handler
                = new SampleHtmlErrorHanlder("500", "MSG_TOKEN", "EXPLANATION_TOKEN", null);
        handler.serve(response);

        assertThat(response.getStatus(), containsString("500"));
        assertThat(outputStream.toString(), containsString("MSG_TOKEN"));
        assertThat(outputStream.toString(), containsString("EXPLANATION_TOKEN"));
    }


    private static class SampleHtmlErrorHanlder extends AbstractHtmlErrorHandler {
        public SampleHtmlErrorHanlder(String status, String message, String explanation, String errorDocumentPath) {
            super(status, message, explanation, errorDocumentPath);
        }
    }
}