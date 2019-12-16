package ro.polak.http.utilities;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static ro.polak.http.ExtraMarchers.utilityClass;

// CHECKSTYLE.OFF: JavadocType
public class FileUtilitiesTest {

    @Test
    public void shouldNotBeInstantiableFinalClass() {
        assertThat(FileUtilities.class, is(utilityClass()));
    }

    @Test
    public void shouldReturnValidExtension() {
        assertThat(FileUtilities.getExtension("file.ext"), is("ext"));
        assertThat(FileUtilities.getExtension("/path/file.ext"), is("ext"));
        assertThat(FileUtilities.getExtension("file"), is(""));
        assertThat(FileUtilities.getExtension(null), is(nullValue()));
    }

    // CHECKSTYLE.OFF: MagicNumber
    @Test
    public void shouldFormatFileSize() {
        assertThat(FileUtilities.fileSizeUnits(1), is("1 B"));
        assertThat(FileUtilities.fileSizeUnits(1024), is("1.00 KB"));
        assertThat(FileUtilities.fileSizeUnits(1025), is("1.00 KB"));
        assertThat(FileUtilities.fileSizeUnits(1048576), is("1.00 MB"));
        assertThat(FileUtilities.fileSizeUnits(1048577), is("1.00 MB"));
        assertThat(FileUtilities.fileSizeUnits(1073741824), is("1.00 GB"));
        assertThat(FileUtilities.fileSizeUnits(1073741825), is("1.00 GB"));
    }
    // CHECKSTYLE.OFF: MagicNumber
}
// CHECKSTYLE.ON: JavadocType
