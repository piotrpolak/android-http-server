package ro.polak.http;

import org.junit.BeforeClass;

import java.io.IOException;
import java.net.ServerSocket;

import ro.polak.http.impl.DefaultServerConfigFactory;

/**
 * https://zeroturnaround.com/rebellabs/the-correct-way-to-use-integration-tests-in-your-build-process/
 */
public class AbstractIT {

    @BeforeClass
    public static void setup() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        ServerConfig serverConfig = (new DefaultServerConfigFactory()).getServerConfig();
        WebServer webServer = new WebServer(serverSocket, serverConfig);
        webServer.startServer();
    }
}
