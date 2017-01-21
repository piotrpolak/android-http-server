package ro.polak.http.servlet;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpResponseWrapperTest {

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowHeadersToBeFlushedTwice() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream outputStream = mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        HttpResponseWrapper httpResponseWrapper = HttpResponseWrapper.createFromSocket(socket);
        try {
            httpResponseWrapper.flushHeaders();
        } catch (IllegalStateException e) {
            fail("Should not throw exception on the first call");
        }

        httpResponseWrapper.flushHeaders();
    }

}