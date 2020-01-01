package ro.polak.http;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.errorhandler.HttpErrorHandlerResolver;
import ro.polak.http.servlet.factory.HttpServletRequestImplFactory;
import ro.polak.http.servlet.factory.HttpServletResponseImplFactory;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
public class ServiceUnavailableHandlerTest {

    private static HttpServletResponseImplFactory factory;
    private static ServiceUnavailableHandler serviceUnavailableHandler;
    private static ByteArrayOutputStream outputStream;
    private static PrintWriter printWriter;

    @Before
    public void setUp() throws Exception {
        outputStream = new ByteArrayOutputStream();
        factory = mock(HttpServletResponseImplFactory.class);
        HttpServletResponseImpl response = mock(HttpServletResponseImpl.class);
        printWriter = new PrintWriter(outputStream);
        when(response.getWriter()).thenReturn(printWriter);

        when(factory.createFromSocket(any(Socket.class))).thenReturn(response);
        serviceUnavailableHandler = new ServiceUnavailableHandler(factory);
    }

    @Test
    public void shouldIgnoreRunnableThatIsNotServerRunnable() throws Exception {
        serviceUnavailableHandler.rejectedExecution(mock(Runnable.class), null);
        verify(factory, never()).createFromSocket(any(Socket.class));
    }

    @Test
    public void shouldHandleServerRunnable() throws Exception {
        ServerRunnable runnable = new ServerRunnable(mock(Socket.class), mock(ServerConfig.class),
                mock(HttpServletRequestImplFactory.class), factory,
                mock(HttpErrorHandlerResolver.class), mock(PathHelper.class));
        serviceUnavailableHandler.rejectedExecution(runnable, null);
        verify(factory, times(1)).createFromSocket(any(Socket.class));
        printWriter.flush();
        assertThat(outputStream.toString(), containsString("503"));
    }
}
// CHECKSTYLE.ON: JavadocType
