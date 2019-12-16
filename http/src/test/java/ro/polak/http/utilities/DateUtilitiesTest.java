package ro.polak.http.utilities;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static ro.polak.http.ExtraMarchers.utilityClass;

// CHECKSTYLE.OFF: JavadocType
public class DateUtilitiesTest {

    @Test
    public void shouldNotBeInstantiableFinalClass() {
        Assert.assertThat(DateUtilities.class, CoreMatchers.is(utilityClass()));
    }

    // CHECKSTYLE.OFF: MagicNumber
    @Test
    public void shouldFormatDate() {
        assertThat(DateUtilities.dateFormat(new Date(1520881821937L)), is("Mon, 12 Mar 2018 19:10:21 GMT"));
    }
    // CHECKSTYLE.ON: MagicNumber
}
// CHECKSTYLE.ON: JavadocType
