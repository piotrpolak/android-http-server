package ro.polak.http;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.errorhandler.HttpErrorHandlerResolver;
import ro.polak.http.servlet.factory.HttpServletRequestImplFactory;
import ro.polak.http.servlet.factory.HttpServletResponseImplFactory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
public class ServerRunnableTest {

    @Test
    public void shouldLogIOExceptionsSilently() throws Exception {

        Socket socket = mock(Socket.class);
        HttpServletResponseImplFactory responseFactory = mock(HttpServletResponseImplFactory.class);

        when(responseFactory.createFromSocket(socket)).thenThrow(new IOException());

        ServerRunnable serverRunnable = new ServerRunnable(socket, mock(ServerConfig.class),
                mock(HttpServletRequestImplFactory.class),
                responseFactory,
                mock(HttpErrorHandlerResolver.class),
                new PathHelper()
        );

        serverRunnable.run();

        verify(socket, times(1)).close();
    }
}
// CHECKSTYLE.ON: JavadocType
