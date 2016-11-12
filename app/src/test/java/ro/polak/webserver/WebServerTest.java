package ro.polak.webserver;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebServerTest {

    @Test
    public void shouldCaptureIOException() throws IOException {
        ServerSocket serverSocket = mock(ServerSocket.class);
        when(serverSocket.accept()).thenThrow(new IOException());
        WebServer webServer = new WebServer(serverSocket, mock(ServerConfig.class));
        try {
            webServer.run();
        } catch (Exception e) {
            if (e instanceof IOException) {
                fail("Should not throw an exception");
            }
        }
    }
}