package ro.polak.http.exception;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

// CHECKSTYLE.OFF: JavadocType
public class ServletExceptionTest {

    @Test
    public void shouldOfferStringConstructor() {
        Exception exception = new ServletException("SomeMessage");
        assertThat(exception.getMessage(), is("SomeMessage"));
    }

    @Test
    public void shouldOfferStringThrowableConstructor() {
        Throwable e = new Exception();
        Exception exception = new ServletException("SomeMessage", e);
        assertThat(exception.getMessage(), is("SomeMessage"));
        assertThat(exception.getCause(), is(e));
    }

    @Test
    public void shouldThrowableConstructor() {
        Throwable e = new Exception();
        Exception exception = new ServletException(e);
        assertThat(exception.getCause(), is(e));
    }
}
// CHECKSTYLE.ON: JavadocType
