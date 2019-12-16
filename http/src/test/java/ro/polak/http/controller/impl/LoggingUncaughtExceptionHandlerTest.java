package ro.polak.http.controller.impl;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
public class LoggingUncaughtExceptionHandlerTest {

    @Test
    public void shouldLogException() {
        Thread.UncaughtExceptionHandler handler
                = new ControllerImpl.LoggingUncaughtExceptionHandler();

        Throwable throwable = mock(Throwable.class);
        when(throwable.getStackTrace()).thenReturn(new StackTraceElement[]{new StackTraceElement("X", "X", "X", 1)});
        handler.uncaughtException(Thread.currentThread(), throwable);

        verify(throwable, times(1)).getStackTrace();
    }
}
// CHECKSTYLE.ON: JavadocType
