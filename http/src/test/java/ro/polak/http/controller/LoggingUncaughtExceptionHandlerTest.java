package ro.polak.http.controller;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoggingUncaughtExceptionHandlerTest {

    @Test
    public void shouldLogException() {
        Thread.UncaughtExceptionHandler handler
                = new MainController.LoggingUncaughtExceptionHandler();

        Throwable throwable = mock(Throwable.class);
        when(throwable.getStackTrace()).thenReturn(new StackTraceElement[]{new StackTraceElement("X", "X", "X", 1)});
        handler.uncaughtException(Thread.currentThread(), throwable);

        verify(throwable, times(1)).getStackTrace();
    }
}