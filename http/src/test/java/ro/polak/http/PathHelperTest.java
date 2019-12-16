package ro.polak.http;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class PathHelperTest {

    private static PathHelper pathHelper = new PathHelper();

    @Test
    public void shouldNotAcceptInvalidCharacters() {
        assertThat(pathHelper.isPathContainingIllegalCharacters("somepath../"), is(false));
        assertThat(pathHelper.isPathContainingIllegalCharacters("../somepath"), is(true));
        assertThat(pathHelper.isPathContainingIllegalCharacters("somepath/../"), is(true));
        assertThat(pathHelper.isPathContainingIllegalCharacters(null), is(true));
    }

    @Test
    public void shouldNormalizeDirectoryPath() {
        assertThat(pathHelper.getNormalizedDirectoryPath("somepath"), is("somepath/"));
        assertThat(pathHelper.getNormalizedDirectoryPath("somepath/"), is("somepath/"));
    }

    @Test
    public void shouldSayWhetherDirectoryPath() {
        assertThat(pathHelper.isDirectoryPath("somepath"), is(false));
        assertThat(pathHelper.isDirectoryPath("somepath/"), is(true));
    }
}
// CHECKSTYLE.ON: JavadocType
