package ro.polak.http.servlet;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

// CHECKSTYLE.OFF: JavadocType
public class UploadedFileTest {

    private static String tempPath = System.getProperty("java.io.tmpdir") + "/";

    @Test
    public void shouldDeleteFileDestory() throws IOException {
        File file = new File(tempPath + "uploadfile.pdf");
        try {
            if (!file.createNewFile()) {
                fail("Unable to create new file.");
            }
            assertThat(file.exists(), is(true));
            UploadedFile uploadedFile = new UploadedFile("myfile", "myfile.pdf", file);
            if (!uploadedFile.destroy()) {
                fail("Unable to destroy uploaded file.");
            }
            assertThat(uploadedFile.getFile().exists(), is(false));

            assertThat(uploadedFile.getFileName(), is("myfile.pdf"));
            assertThat(uploadedFile.getPostFieldName(), is("myfile"));
        } finally {
            cleanupFile(file);
        }
    }

    @Test
    public void shouldHandleDestroyWhenFileDeletedManually() throws IOException {
        File file = new File(tempPath + "uploadfile.pdf");
        try {
            if (!file.createNewFile()) {
                fail("Unable to create new file.");
            }
            assertThat(file.exists(), is(true));
            UploadedFile uploadedFile = new UploadedFile("myfile", "myfile.pdf", file);

            if (!file.delete()) {
                fail("Unable to delete file.");
            }

            if (uploadedFile.destroy()) {
                fail("File should be already deleted.");
            }
            assertThat(uploadedFile.getFile().exists(), is(false));

        } finally {
            cleanupFile(file);
        }
    }

    @Test
    public void shouldNotDeleteFileThatWasMoved() throws IOException {
        File file = new File(tempPath + "uploadfile.pdf");
        try {
            if (!file.createNewFile()) {
                throw new IOException("Unable to create " + file.getAbsolutePath());
            }

            assertThat(file.exists(), is(true));
            UploadedFile uploadedFile = new UploadedFile("myfile", "myfile.pdf", file);
            File movedFile = new File(tempPath + "uploadfile123.pdf");
            if (movedFile.exists() && !movedFile.delete()) {
                throw new IOException("Unable to delete " + movedFile.getAbsolutePath());
            }
            assertThat(movedFile.exists(), is(false));
            assertThat(uploadedFile.getFile().renameTo(movedFile), is(true));
            assertThat(uploadedFile.getFile().exists(), is(false));
            if (uploadedFile.destroy()) {
                fail("File should be already moved.");
            }
            assertThat(movedFile.exists(), is(true));
            if (!movedFile.delete()) {
                throw new IOException("Unable to delete " + movedFile.getAbsolutePath());
            }

        } finally {
            cleanupFile(file);
        }
    }

    private void cleanupFile(final File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("Unable to delete " + file.getAbsolutePath());
        }
    }
}
// CHECKSTYLE.ON: JavadocType
