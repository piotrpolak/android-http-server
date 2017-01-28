package ro.polak.http;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
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
    private static File staticFile;
    private static File httpdConfigFile;

    @BeforeClass
    public static void setUp() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket();


            httpdConfigFile = new File("/tmp/webserver/httpd.conf");
            httpdConfigFile.createNewFile();

            ServerConfig serverConfig = getServerConfig();

            File documentRoot = new File(serverConfig.getDocumentRootPath());
            if (!documentRoot.exists()) {
                documentRoot.mkdir();
            }

            staticFile = new File(serverConfig.getDocumentRootPath() + "staticfile.html");
            staticFile.createNewFile();

            WebServer webServer = new WebServer(serverSocket, serverConfig);
            if (!webServer.startServer()) {
                fail("Unable to start server");
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        if (staticFile != null) {
            staticFile.delete();
        }
        if (httpdConfigFile != null) {
            httpdConfigFile.delete();
        }
    }

    private static ServerConfig getServerConfig() {
        return (new DefaultServerConfigFactory() {
            @Override
            protected String getBasePath() {
                return getTempPath();
            }
        }).getServerConfig();
    }

    protected Socket getSocket() throws IOException {
        Socket socket;
        socket = new Socket(HOST, PORT);
        socket.setSoTimeout(0);
        return socket;
    }
}
