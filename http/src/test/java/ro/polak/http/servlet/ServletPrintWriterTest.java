package ro.polak.http.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public class ServletPrintWriterTest {

    @Test
    public void shouldNotAllowToWriteEndMoreThanOnce() {
        final ServletPrintWriter servletPrintWriter = new ServletPrintWriter(mock(ServletOutputStream.class));
        try {
            servletPrintWriter.writeEnd();
        } catch (Throwable e) {
            fail("Should not throw any exception here");
        }

        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                servletPrintWriter.writeEnd();
            }
        });
    }
}
// CHECKSTYLE.ON: JavadocType
