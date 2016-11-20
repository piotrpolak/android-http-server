package ro.polak.http;

import org.junit.BeforeClass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ro.polak.http.impl.DefaultServerConfigFactory;

/**
 * https://zeroturnaround.com/rebellabs/the-correct-way-to-use-integration-tests-in-your-build-process/
 */
public class AbstractIT {

    protected final String HOST = "localhost";
    protected final int PORT = 8080;
    private static ServerSocket serverSocket;

    protected Socket getSocket() throws IOException {
        Socket socket;
        socket = new Socket(HOST, PORT);
        socket.setSoTimeout(0);
        return socket;
    }

    @BeforeClass
    public static void setup() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket();
            ServerConfig serverConfig = (new DefaultServerConfigFactory()).getServerConfig();
            WebServer webServer = new WebServer(serverSocket, serverConfig);
            webServer.startServer();
        }
    }
}
