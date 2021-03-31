package ro.polak.http;

import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import ro.polak.http.cli.DefaultServerConfigFactory;
import ro.polak.http.configuration.ServerConfig;

import static org.junit.jupiter.api.Assertions.fail;

// CHECKSTYLE.OFF: JavadocType
public class AbstractIT {

    protected static final String HOST = "localhost";

    private static int port = 0;
    private static ServerSocket serverSocket;
    private static File httpdConfigFile;
    private static String tempDirectory;

    @BeforeAll
    public static void setUp() throws IOException {
        if (serverSocket == null) {
            serverSocket = new ServerSocket();

            tempDirectory = FileUtils.createTempDirectory();

            WebServer webServer = new WebServer(serverSocket, getPreparedConfig());
            if (!webServer.startServer()) {
                fail("Unable to start server");
            }
            setPort(serverSocket.getLocalPort());
        }
    }

    protected static int getPort() {
        return port;
    }

    protected static void setPort(final int port) {
        AbstractIT.port = port;
    }

    /**
     * Builds the full request URL.
     */
    protected String getFullUrl(final String path) {
        return "http://" + HOST + ":" + port + path;
    }

    /**
     * Builds socket to the predefined host and port.
     */
    protected Socket getSocket() throws IOException {
        Socket socket;
        socket = new Socket(HOST, port);
        socket.setSoTimeout(0);
        return socket;
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
        handleFile(serverConfig, "static" + File.separator + "index.html", "Index file");

        return serverConfig;
    }

    private static void handleFile(final ServerConfig serverConfig, final String relativePath, final String contents)
            throws IOException {
        File documentRoot = new File(serverConfig.getDocumentRootPath());
        if (!documentRoot.exists() && !documentRoot.mkdir()) {
            throw new IOException("Unable to mkdir " + documentRoot.getAbsolutePath());
        }

        File file = new File(serverConfig.getDocumentRootPath() + relativePath);
        if (file.exists() && !file.delete()) {
            throw new IOException("Unable to delete " + file.getAbsolutePath());
        }

        if (relativePath.contains(File.separator)) {
            mkdirs(file);
        }

        if (!file.createNewFile()) {
            throw new IOException("Unable to create " + file.getAbsolutePath());
        }

        PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8.name());
        writer.print(contents);
        writer.close();
    }

    private static void mkdirs(final File file) throws IOException {
        File directory = new File(file.getParentFile().getAbsolutePath());
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Unable to mkdir " + directory.getAbsolutePath());
        }
    }

    private static ServerConfig getServerConfig() {
        return (new DefaultServerConfigFactory() {
            @Override
            protected String getBasePath() {
                return getTempPath();
            }

            @Override
            protected String getTempPath() {
                return tempDirectory;
            }

            /**
             * Starts the server at the first available port.
             *
             * @return
             */
            @Override
            protected int getDefaultListenPort() {
                return 0;
            }
        }).getServerConfig();
    }
}
// CHECKSTYLE.ON: JavadocType

