package ro.polak.http.utilities;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class DateProviderTest {

    @Test
    public void shouldReturnAValidDate() throws Exception {
        DateProvider dateProvider = new DateProvider();
        assertThat(dateProvider.now().getTime(), is(lessThanOrEqualTo(new Date().getTime())));
    }
}
// CHECKSTYLE.ON: JavadocType
