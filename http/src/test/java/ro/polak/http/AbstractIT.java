package ro.polak.http;

import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ro.polak.http.cli.DefaultServerConfigFactory;

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

            WebServer webServer = new WebServer(serverSocket, getPreparedConfig());
            if (!webServer.startServer()) {
                fail("Unable to start server");
            }
        }
    }

    protected String getFullUrl(String path) {
        return "http://" + HOST + ":" + PORT + path;
    }

    private static ServerConfig getPreparedConfig() throws IOException {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "webserver" + File.separator;

        File workingDirectory = new File(tempPath);
        if (!workingDirectory.exists()) {
            if (!workingDirectory.mkdir()) {
                throw new IOException("Unable to mkdir " + workingDirectory.getAbsolutePath());
            }
        }

        httpdConfigFile = new File(tempPath + "httpd.properties");
        if (httpdConfigFile.exists()) {
            if (!httpdConfigFile.delete()) {
                throw new IOException("Unable to delete " + httpdConfigFile.getAbsolutePath());
            }
        }
        if (!httpdConfigFile.createNewFile()) {
            throw new IOException("Unable to create " + httpdConfigFile.getAbsolutePath());
        }

        ServerConfig serverConfig = getServerConfig();

        File documentRoot = new File(serverConfig.getDocumentRootPath());
        if (!documentRoot.exists()) {
            if (!documentRoot.mkdir()) {
                throw new IOException("Unable to mkdir " + documentRoot.getAbsolutePath());
            }
        }

        staticFile = new File(serverConfig.getDocumentRootPath() + "staticfile.html");
        if (staticFile.exists()) {
            if (!staticFile.delete()) {
                throw new IOException("Unable to delete " + staticFile.getAbsolutePath());
            }
        }
        if (!staticFile.createNewFile()) {
            throw new IOException("Unable to create " + staticFile.getAbsolutePath());
        }

        PrintWriter writer = new PrintWriter(staticFile, "UTF-8");
        writer.print("Static file");
        writer.close();

        return serverConfig;
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
