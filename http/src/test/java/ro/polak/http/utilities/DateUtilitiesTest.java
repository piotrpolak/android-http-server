package ro.polak.http.utilities;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DateUtilitiesTest {

    @Test
    public void shouldFormatDate() {
        assertThat(DateUtilities.dateFormat(new Date(1520881821937L)), is("Mon, 12 Mar 2018 19:10:21 GMT"));
    }
}