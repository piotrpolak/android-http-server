package ro.polak.http;

import org.junit.BeforeClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ro.polak.http.impl.DefaultServerConfigFactory;

import static junit.framework.TestCase.fail;

/**
 * https://zeroturnaround.com/rebellabs/the-correct-way-to-use-integration-tests-in-your-build-process/
 */
public class AbstractIT {

    private static ServerSocket serverSocket;
    protected final String HOST = "localhost";
    protected final int PORT = 8080;

    @BeforeClass
    public static void setup() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket();
            WebServer webServer = new WebServer(serverSocket, getServerConfig());
            if (!webServer.startServer()) {
                fail("Unable to start server");
            }
        }
    }

    private static ServerConfig getServerConfig() {
        return (new DefaultServerConfigFactory()).getServerConfig();
    }

    protected Socket getSocket() throws IOException {
        Socket socket;
        socket = new Socket(HOST, PORT);
        socket.setSoTimeout(0);
        return socket;
    }
}
