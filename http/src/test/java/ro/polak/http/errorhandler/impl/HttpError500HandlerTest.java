package ro.polak.http.errorhandler.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import ro.polak.http.servlet.impl.HttpServletResponseImpl;

// CHECKSTYLE.OFF: JavadocType
@ExtendWith(MockitoExtension.class)
public final class HttpError500HandlerTest {

    private static HttpServletResponseImpl httpServletResponse;
    private static PrintWriter printWriter;
    private static HttpError500Handler httpError500Handler;

    @Captor
    private ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);

    @BeforeEach
    public void setUp() {
        httpServletResponse = mock(HttpServletResponseImpl.class);
        printWriter = mock(PrintWriter.class);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);
        httpError500Handler = new HttpError500Handler();
    }

    @Test
    public void shouldDisplayClassNameAndExceptionMessage() throws IOException {
        Exception e = new RuntimeException("FancyExceptionMessage");

        httpError500Handler.setReason(e).serve(httpServletResponse);

        verify(printWriter).write(arg.capture());
        String output = arg.getValue();
        assertThat(output, containsString(">FancyExceptionMessage java.lang.RuntimeException<"));
    }

    @Test
    public void shouldDisplayClassNameAndNoExceptionMessageForEmptyValue() throws IOException {
        Exception e = new RuntimeException();

        httpError500Handler.setReason(e).serve(httpServletResponse);

        verify(printWriter).write(arg.capture());
        String output = arg.getValue();
        assertThat(output, containsString(">java.lang.RuntimeException<"));
    }

    @Test
    public void shouldDisplayClassNameAndNoExceptionMessageForNullValue() throws IOException {
        String msg = null;
        Exception e = new RuntimeException(msg);

        httpError500Handler.setReason(e).serve(httpServletResponse);

        verify(printWriter).write(arg.capture());
        String output = arg.getValue();
        assertThat(output, containsString(">java.lang.RuntimeException<"));
    }
}
// CHECKSTYLE.ON: JavadocType
