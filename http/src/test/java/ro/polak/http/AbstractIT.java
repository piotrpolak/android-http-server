package ro.polak.http;

import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ro.polak.http.cli.DefaultServerConfigFactory;
import ro.polak.http.configuration.ServerConfig;

import static junit.framework.TestCase.fail;

/**
 * https://zeroturnaround.com/rebellabs/the-correct-way-to-use-integration-tests-in-your-build-process/
 */
public class AbstractIT {

    private static ServerSocket serverSocket;
    protected final String HOST = "localhost";
    protected final int PORT = 8080;
    private static File httpdConfigFile;
    private static String tempDirectory;

    @BeforeClass
    public static void setUp() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket();

            tempDirectory = FileUtils.createTempDirectory();

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
        File workingDirectory = new File(tempDirectory);
        if (!workingDirectory.exists() && !workingDirectory.mkdir()) {
            throw new IOException("Unable to mkdir " + workingDirectory.getAbsolutePath());
        }

        httpdConfigFile = new File(tempDirectory + "httpd.properties");
        if (httpdConfigFile.exists() && !httpdConfigFile.delete()) {
            throw new IOException("Unable to delete " + httpdConfigFile.getAbsolutePath());
        }
        if (!httpdConfigFile.createNewFile()) {
            throw new IOException("Unable to create " + httpdConfigFile.getAbsolutePath());
        }

        ServerConfig serverConfig = getServerConfig();

        handleFile(serverConfig, "staticfile.html", "Static file");
        handleFile(serverConfig, "index.html", "Index file");

        return serverConfig;
    }

    private static void handleFile(ServerConfig serverConfig, String relativePath, String contents) throws IOException {
        File documentRoot = new File(serverConfig.getDocumentRootPath());
        if (!documentRoot.exists() && !documentRoot.mkdir()) {
            throw new IOException("Unable to mkdir " + documentRoot.getAbsolutePath());
        }

        File file = new File(serverConfig.getDocumentRootPath() + relativePath);
        if (file.exists() && !file.delete()) {
            throw new IOException("Unable to delete " + file.getAbsolutePath());
        }
        if (!file.createNewFile()) {
            throw new IOException("Unable to create " + file.getAbsolutePath());
        }

        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.print(contents);
        writer.close();
    }

    private static ServerConfig getServerConfig() throws IOException {

        return (new DefaultServerConfigFactory() {
            @Override
            protected String getBasePath() {
                return getTempPath();
            }

            @Override
            protected String getTempPath() {
                return tempDirectory;
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
