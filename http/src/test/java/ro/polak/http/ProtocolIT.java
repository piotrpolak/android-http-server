package ro.polak.http;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * @url https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 */
public class ProtocolIT extends AbstractIT {

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
            socket = getSocket();
            out = socket.getOutputStream();
            out.write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (in.readLine() != null) {
                // Simulate reading
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
    public void shouldServeDirectoryIndex() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/example/")
                .withHost(HOST + ":" + PORT)
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out = null;
        try {
            socket = getSocket();
            out = socket.getOutputStream();
            out.write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            int numberOfLinesRead = 0;
            while ((line = in.readLine()) != null) {
                if (++numberOfLinesRead == 1) {
                    assertThat(line, startsWith("HTTP/1.1 200"));
                }
            }

        } catch (IOException e) {
            fail("The test failed too early due IOException" + e.getMessage());
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

        socket = getSocket();
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
    public void shouldReturn403ForbiddenOnIllegalPath() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("../../../index.html")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;

        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 403"));
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
                .method("CONNECT", "/") // Connect is not yet implemented
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = getSocket();
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

    @Test
    public void shouldReturn414URITooLong() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get(getTooLongUri())
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 414"));
                break;
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }

    private String getTooLongUri() {
        // 2048 characters seems reasonable
        // see http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url-in-different-browsers
        int length = 2048;
        char[] uri = new char[length + 1];
        uri[0] = '/';
        for (int i = 1; i <= length; i++) {
            uri[i] = 'a';
        }

        return new String(uri);
    }

    @Test
    public void shouldReturn413PayloadTooLarge() {
        // maxPostSize 2mb
        // TODO implement
    }


    @Test
    public void shouldReturn411LengthRequiredForPost() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .method("POST", "/example/") // Connect is not yet implemented
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 411"));
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }

    @Test
    public void shouldReturn411LengthRequiredForPostMultiPart() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .method("POST", "/example/") // Connect is not yet implemented
                .withHeader("Content-Type", "multipart/mixed; boundary=s9xksnd72SSHu")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 411"));
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }

    @Test
    public void shouldReturn400BadRequestOnUnrecognizedMethod() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .method("ABC", "/")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 400"));
                break;
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }

    @Test
    public void shouldReturn400BadRequestOnTooLongMethod() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .method("ABCABCABCABCABC", "/")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;
        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 400"));
                break;
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        socket.close();
    }

    @Test
    public void shouldHangSilentlyOnClosingSocket() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/43524938257493852435/SOMEUNKNOWNURL.html")
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out;

        socket = getSocket();
        out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 404"));
                socket.close();
                break;
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }
    }

    @Test
    public void shouldReturn400BadRequest() {
        // TODO implement
    }

    @Test
    public void shouldReturn416RangeNotSatisfiable() {
        // TODO implement
    }

    @Test
    public void shouldReturn431RequestHeaderFieldsTooLarge() {
        // TODO implement
    }

    @Test
    public void shouldReturn500InternalServerError() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/example/InternalServerError.dhtml")
                .withHost(HOST + ":" + PORT)
                .withCloseConnection()
                .toString();

        Socket socket = null;
        OutputStream out = null;
        try {
            socket = getSocket();
            out = socket.getOutputStream();
            out.write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            int numberOfLinesRead = 0;
            while ((line = in.readLine()) != null) {
                if (++numberOfLinesRead == 1) {
                    assertThat(line, startsWith("HTTP/1.1 500"));
                }
            }

        } catch (IOException e) {
            fail("The test failed too early due IOException" + e.getMessage());
        }

        socket.close();
    }

//    @Test
//    public void shouldReturn505HTTPVersionNotSupported() {
//        String requestBody = RequestBuilder.defaultBuilder()
//                .get("SomeUrl.html")
//                .withHost(HOST + ":" + PORT)
//                .withProtocol("HTTP/9.0")
//                .withCloseConnection()
//                .toString();
//
//        Socket socket = null;
//        OutputStream out = null;
//        try {
//            socket = getSocket();
//            out = socket.getOutputStream();
//            out.write(requestBody.getBytes());
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String line;
//            int numberOfLinesRead = 0;
//            while ((line = in.readLine()) != null) {
//                if (++numberOfLinesRead == 1) {
//                    assertThat(line, startsWith("HTTP/1.1 503"));
//                }
//            }
//
//        } catch (IOException e) {
//            fail("The test failed too early due IOException" + e.getMessage());
//        }
//    }
}
