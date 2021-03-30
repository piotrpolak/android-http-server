package ro.polak.http.utilities;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

// CHECKSTYLE.OFF: JavadocType
public class DateProviderTest {

    @Test
    public void shouldReturnAValidDate() throws Exception {
        DateProvider dateProvider = new DateProvider();
        assertThat(dateProvider.now().getTime(), is(lessThanOrEqualTo(new Date().getTime())));
    }
}
// CHECKSTYLE.ON: JavadocType
