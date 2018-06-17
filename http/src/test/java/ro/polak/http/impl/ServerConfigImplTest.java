package ro.polak.http.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import ro.polak.http.FileUtils;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.impl.ServerConfigImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class ServerConfigImplTest {

    public static final String OVERWRITTEN_VALUE = "OVERWRITTEN";
    public static final String ORIGINAL_VALUE = "somevalue";
    public static final String ADDITIONAL_ATTRIBUTE_NAME = "additional.attribute";

    private static final String DEFAULT_CONFIG_DATA = "server.port=8090\n" +
            "server.static.path=wwwx\n" +
            "server.static.directoryIndex=index.php,index.html\n" +
            "server.mimeType.defaultMimeType=mime/text\n" +
            "server.mimeType.filePath=mime.mime\n" +
            "server.maxThreads=3\n" +
            "server.keepAlive.enabled=true\n" +
            "server.errorDocument.404=error404.html\n" +
            "server.errorDocument.403=error403.html\n" +
            ADDITIONAL_ATTRIBUTE_NAME + "=" + ORIGINAL_VALUE + "\n";

    private static String workingDirectory;
    private static String tempDirectory;
    private static File configFile;
    private static File mimeFile;


    @BeforeClass
    public static void setuUp() throws IOException {
        workingDirectory = FileUtils.createTempDirectory();
        tempDirectory = FileUtils.createTempDirectory();
    }

    @AfterClass
    public static void cleanUp() {
        new File(workingDirectory).delete();
        new File(tempDirectory).delete();
    }

    @After
    public void cleanUpAfterTest() throws IOException {
        if (configFile != null && configFile.exists() && !configFile.delete()) {
            throw new IOException("Unable to delete " + configFile.getAbsolutePath());
        }

        if (mimeFile != null && mimeFile.exists() && !mimeFile.delete()) {
            throw new IOException("Unable to delete " + mimeFile.getAbsolutePath());
        }
    }

    public void writeFiles(String configData) throws IOException {
        configFile = new File(workingDirectory + "httpd.properties");
        mimeFile = new File(workingDirectory + "mime.mime");
        try {
            mimeFile.createNewFile();
        } catch (IOException e) {
        }

        FileUtils.writeToFile(configFile, configData);
    }

    @Test
    public void shouldCreateFromPath() throws IOException {
        writeFiles(DEFAULT_CONFIG_DATA + "DefaultMimeType mime/text\n");

        ServerConfig serverConfig = ServerConfigImpl.createFromPath(workingDirectory, tempDirectory);
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

        ServerConfig serverConfig = ServerConfigImpl.createFromPath(workingDirectory, tempDirectory);
        assertThat(serverConfig.getMimeTypeMapping().getMimeTypeByExtension("ANY"), is("mime/text"));
    }

    @Test
    public void shouldPreferSystemPropertiesOverFileDefinedOnes() throws IOException {
        writeFiles(DEFAULT_CONFIG_DATA);
        ServerConfig serverConfig = ServerConfigImpl.createFromPath(workingDirectory, tempDirectory);
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(ORIGINAL_VALUE));

        System.setProperty(ADDITIONAL_ATTRIBUTE_NAME, OVERWRITTEN_VALUE);
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(OVERWRITTEN_VALUE));

        System.clearProperty(ADDITIONAL_ATTRIBUTE_NAME);
        assertThat(serverConfig.getAttribute(ADDITIONAL_ATTRIBUTE_NAME), is(ORIGINAL_VALUE));
    }
}