package ro.polak.http.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import ro.polak.http.FileUtils;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.impl.ServerConfigImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public final class ServerConfigImplTest {

    private static final String OVERWRITTEN_VALUE = "OVERWRITTEN";
    private static final String ORIGINAL_VALUE = "somevalue";
    private static final String ADDITIONAL_ATTRIBUTE_NAME = "additional.attribute";
    private static final int DEFAULT_LISTEN_PORT = 8080;

    private Properties backup;

    private static final String DEFAULT_CONFIG_DATA = "server.port=8090\n"
            + "server.static.path=wwwx\n"
            + "server.static.directoryIndex=index.php,index.html\n"
            + "server.mimeType.defaultMimeType=mime/text\n"
            + "server.mimeType.filePath=mime.mime\n"
            + "server.maxThreads=3\n"
            + "server.keepAlive.enabled=true\n"
            + "server.errorDocument.404=error404.html\n"
            + "server.errorDocument.403=error403.html\n"
            + ADDITIONAL_ATTRIBUTE_NAME + "=" + ORIGINAL_VALUE + "\n";

    private static String workingDirectory;
    private static String tempDirectory;
    private static File configFile;
    private static File mimeFile;


    @BeforeAll
    public static void setuUp() throws IOException {
        workingDirectory = FileUtils.createTempDirectory();
        tempDirectory = FileUtils.createTempDirectory();
    }

    @AfterAll
    public static void cleanUp() {
        new File(workingDirectory).delete();
        new File(tempDirectory).delete();
    }

    @BeforeEach
    public void setup() {
        backup = System.getProperties();
        System.setProperties(new Properties());
    }

    @AfterEach
    public void cleanUpAfterTest() throws IOException {
        if (configFile != null && configFile.exists() && !configFile.delete()) {
            throw new IOException("Unable to delete " + configFile.getAbsolutePath());
        }

        if (mimeFile != null && mimeFile.exists() && !mimeFile.delete()) {
            throw new IOException("Unable to delete " + mimeFile.getAbsolutePath());
        }

        System.setProperties(backup);
    }

    @Test
    public void shouldCreateFromPath() throws IOException {
        writeFiles(DEFAULT_CONFIG_DATA + "DefaultMimeType mime/text\n");

        ServerConfig serverConfig = ServerConfigImpl.createFromPath(workingDirectory, tempDirectory, DEFAULT_LISTEN_PORT);
        assertThat(serverConfig.getTempPath(), is(tempDirectory));
        assertThat(serverConfig.getBasePath(), is(workingDirectory));
        assertThat(serverConfig.getDocumentRootPath(), is(workingDirectory + "wwwx"));
        assertThat(serverConfig.getDirectoryIndex(), hasItem("index.php"));
        assertThat(serverConfig.getDirectoryIndex(), hasItem("index.html"));
        assertThat(serverConfig.getDirectoryIndex().size(), is(2));
        assertThat(serverConfig.getErrorDocument403Path(), is(workingDirectory + "error403.html"));
        assertThat(serverConfig.getErrorDocument404Path(), is(workingDirectory + "error404.html"));
        assertThat(serverConfig.getListenPort(), is(8090));
        assertThat(serverConfig.getMaxServerThreads(), is(3));
        assertThat(serverConfig.isKeepAlive(), is(true));
        assertThat(serverConfig.getMimeTypeMapping().getMimeTypeByExtension("ANY"), is("mime/text"));
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(ORIGINAL_VALUE));

    }

    @Test
    public void shouldSetDefaultMimeType() throws IOException {
        writeFiles(DEFAULT_CONFIG_DATA);

        ServerConfig serverConfig = ServerConfigImpl.createFromPath(workingDirectory, tempDirectory, DEFAULT_LISTEN_PORT);
        assertThat(serverConfig.getMimeTypeMapping().getMimeTypeByExtension("ANY"), is("mime/text"));
    }

    @Test
    public void shouldPreferSystemPropertiesOverFileDefinedOnes() throws IOException {
        writeFiles(DEFAULT_CONFIG_DATA);
        ServerConfig serverConfig = ServerConfigImpl.createFromPath(workingDirectory, tempDirectory, DEFAULT_LISTEN_PORT);
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(ORIGINAL_VALUE));

        System.setProperty(ADDITIONAL_ATTRIBUTE_NAME, OVERWRITTEN_VALUE);
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(OVERWRITTEN_VALUE));

        System.clearProperty(ADDITIONAL_ATTRIBUTE_NAME);
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(ORIGINAL_VALUE));
    }

    @Test
    public void shouldReadSystemPropertiesWhenNoFileIsPresent() throws IOException {
        System.setProperty("server.port", "9090");
        System.setProperty("server.static.path", "path");
        System.setProperty("server.static.directoryIndex", "i.php,i.html");
        System.setProperty("server.maxThreads", "66");
        System.setProperty("server.keepAlive.enabled", "false");
        System.setProperty("server.errorDocument.404", "ERROR404.html");
        System.setProperty("server.errorDocument.403", "ERROR403.html");
        System.setProperty(ADDITIONAL_ATTRIBUTE_NAME, "YET_ANOTHER_VALUE");

        ServerConfig serverConfig = new ServerConfigImpl("/tmp/", "/tmp/", DEFAULT_LISTEN_PORT, new Properties());
        assertThat(serverConfig.getDocumentRootPath(), is("/tmp/path"));
        assertThat(serverConfig.getDirectoryIndex(), hasItem("i.php"));
        assertThat(serverConfig.getDirectoryIndex(), hasItem("i.html"));
        assertThat(serverConfig.getDirectoryIndex().size(), is(2));
        assertThat(serverConfig.getErrorDocument403Path(), is("/tmp/ERROR403.html"));
        assertThat(serverConfig.getErrorDocument404Path(), is("/tmp/ERROR404.html"));
        assertThat(serverConfig.getListenPort(), is(9090));
        assertThat(serverConfig.getMaxServerThreads(), is(66));
        assertThat(serverConfig.isKeepAlive(), is(false));
//        assertThat(serverConfig.getMimeTypeMapping().getMimeTypeByExtension("ANY"), is("mime/text"));
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is("YET_ANOTHER_VALUE"));
    }

    private void writeFiles(final String configData) throws IOException {
        configFile = new File(workingDirectory + "httpd.properties");
        mimeFile = new File(workingDirectory + "mime.mime");
        try {
            mimeFile.createNewFile();
        } catch (IOException e) {
        }

        FileUtils.writeToFile(configFile, configData);
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
