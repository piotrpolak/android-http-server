package ro.polak.http.exception;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

// CHECKSTYLE.OFF: JavadocType
public class UnexpectedSituationExceptionTest {

    @Test
    public void shouldOfferStringConstructor() {
        Exception exception = new UnexpectedSituationException("SomeMessage");
        assertThat(exception.getMessage(), is("SomeMessage"));
    }

    @Test
    public void shouldOfferStringThrowableConstructor() {
        Throwable e = new Exception();
        Exception exception = new UnexpectedSituationException("SomeMessage", e);
        assertThat(exception.getMessage(), is("SomeMessage"));
        assertThat(exception.getCause(), is(e));
    }

    @Test
    public void shouldThrowableConstructor() {
        Throwable e = new Exception();
        Exception exception = new UnexpectedSituationException(e);
        assertThat(exception.getCause(), is(e));
    }
}
// CHECKSTYLE.ON: JavadocType
