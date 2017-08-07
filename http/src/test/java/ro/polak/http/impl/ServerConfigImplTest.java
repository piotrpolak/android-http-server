package ro.polak.http.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ro.polak.http.ServerConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

public class ServerConfigImplTest {

    private String tempDir = System.getProperty("java.io.tmpdir") + "/";
    private File configFile;
    private File mimeFile;

    @Before
    public void setUp() {
        configFile = new File(tempDir + "httpd.conf");
        mimeFile = new File(tempDir + "mime.mime");
        try {
            mimeFile.createNewFile();
        } catch (IOException e) {
        }

        String configData = "Listen 8090\n" +
                "DocumentRoot wwwx\n" +
                "ErrorDocument404 error404.html\n" +
                "ErrorDocument403 error403.html\n" +
                "MaxThreads 3\n" +
                "KeepAlive On\n" +
                "DefaultMimeType mime/text\n" +
                "MimeType mime.mime\n" +
                "ServletMappedExtension dddd\n" +
                "DirectoryIndex index.php index.html\n";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(configData);

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws IOException {
        if (configFile != null) {
            if (!configFile.delete()) {
                throw new IOException("Unable to delete " + configFile.getAbsolutePath());
            }
        }

        if (mimeFile != null) {
            if (!mimeFile.delete()) {
                throw new IOException("Unable to delete " + mimeFile.getAbsolutePath());
            }
        }
    }

    @Test
    public void shouldCreateFromPath() throws IOException {
        ServerConfig serverConfig = ServerConfigImpl.createFromPath(tempDir, tempDir);
        assertThat(serverConfig.getBasePath(), is(tempDir));
        assertThat(serverConfig.getDocumentRootPath(), is(tempDir + "wwwx"));
        assertThat(serverConfig.getServletMappedExtension(), is("dddd"));
        assertThat(serverConfig.getDirectoryIndex(), hasItem("index.php"));
        assertThat(serverConfig.getDirectoryIndex(), hasItem("index.html"));
        assertThat(serverConfig.getErrorDocument403Path(), is(tempDir + "error403.html"));
        assertThat(serverConfig.getErrorDocument404Path(), is(tempDir + "error404.html"));
        assertThat(serverConfig.getListenPort(), is(8090));
        assertThat(serverConfig.getMaxServerThreads(), is(3));
        assertThat(serverConfig.isKeepAlive(), is(true));
    }
}