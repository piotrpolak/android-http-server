package ro.polak.webserver;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

public class ProtocolIT extends AbstractIT {

    private String HOST = "localhost";
    private int PORT = 8080;

    @Test(expected = IOException.class)
    public void shouldCloseSocketAfterCloseConnectionRequest() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/example/Index.dhtml")
                .withHost(HOST + ":" + PORT)
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out = null;
        try {
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(0);
            out = socket.getOutputStream();
            out.write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
            }
        } catch (IOException e) {
            fail("The test failed too early due IOException" + e.getMessage());
        }

        int i = 0;
        while (i++ < 10) {
            // The following code will cause error on a closed socket
            Thread.sleep(100);
            out.write("X".getBytes());
            out.flush();
        }
    }

    @Test
    public void shouldReturn404NotFound() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/43524938257493852435/SOMEUNKNOWNURL.html")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;

        socket = new Socket(HOST, PORT);
        socket.setSoTimeout(0);
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 404"));
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }

    @Test
    public void shouldReturn405MethodNotAllowed() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .method("UNKNOWN", "/")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = new Socket(HOST, PORT);
        socket.setSoTimeout(0);
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 405"));
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }
}
