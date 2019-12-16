package ro.polak.http.servlet;

import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public class ServletPrintWriterTest {

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowToWriteEndMoreThanOnce() {
        ServletPrintWriter servletPrintWriter = new ServletPrintWriter(mock(ServletOutputStream.class));
        try {
            servletPrintWriter.writeEnd();
        } catch (Throwable e) {
            fail("Should not throw any exception here");
        }
        servletPrintWriter.writeEnd();
    }
}
// CHECKSTYLE.ON: JavadocType
