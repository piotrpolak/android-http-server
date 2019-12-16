package ro.polak.http.utilities;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static ro.polak.http.ExtraMarchers.utilityClass;

// CHECKSTYLE.OFF: JavadocType
public class StringUtilitiesTest {

    @Test
    public void shouldNotBeInstantiableFinalClass() {
        assertThat(StringUtilities.class, is(utilityClass()));
    }

    @Test
    public void shouldGenerateTwoDifferentRandomStrings() {
        String s1 = StringUtilities.generateRandom();
        String s2 = StringUtilities.generateRandom();

        // CHECKSTYLE.OFF: MagicNumber
        assertThat(s1.length(), is(32));
        assertThat(s2.length(), is(32));
        // CHECKSTYLE.ON: MagicNumber
        assertThat(s1, is(not(s2)));
    }

    @Test
    public void shouldDetectEmptyStrings() {
        assertThat(StringUtilities.isEmpty(null), is(true));
        assertThat(StringUtilities.isEmpty(""), is(true));

        assertThat(StringUtilities.isEmpty(" "), is(false));
        assertThat(StringUtilities.isEmpty("a"), is(false));
    }
}
// CHECKSTYLE.ON: JavadocType
