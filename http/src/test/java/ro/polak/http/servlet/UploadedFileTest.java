package ro.polak.http.servlet;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UploadedFileTest {

    private static String tempPath = System.getProperty("java.io.tmpdir") + "/";

    @Test
    public void shouldDeleteFileDestory() throws IOException {
        File file = new File(tempPath + "uploadfile.pdf");
        file.createNewFile();
        assertThat(file.exists(), is(true));
        UploadedFile uploadedFile = new UploadedFile("myfile", "myfile.pdf", file);
        uploadedFile.destroy();
        assertThat(uploadedFile.getFile().exists(), is(false));

        assertThat(uploadedFile.getFileName(), is("myfile.pdf"));
        assertThat(uploadedFile.getPostFieldName(), is("myfile"));
    }

    @Test
    public void shouldNotDeleteFileThatWasMoved() throws IOException {
        File file = new File(tempPath + "uploadfile.pdf");
        file.createNewFile();
        assertThat(file.exists(), is(true));
        UploadedFile uploadedFile = new UploadedFile("myfile", "myfile.pdf", file);
        File movedFile = new File(tempPath + "uploadfile123.pdf");
        movedFile.delete();
        assertThat(movedFile.exists(), is(false));
        assertThat(uploadedFile.getFile().renameTo(movedFile), is(true));
        uploadedFile.destroy();
        assertThat(movedFile.exists(), is(true));
        movedFile.delete();
    }
}