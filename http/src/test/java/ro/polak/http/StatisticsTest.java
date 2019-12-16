package ro.polak.http;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static ro.polak.http.ExtraMarchers.utilityClass;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class StatisticsTest {

    @Test
    public void shouldNotBeInstantiableFinalClass() {
        assertThat(Statistics.class, is(utilityClass()));
    }

    @Before
    public void setUp() {
        Statistics.reset();
    }

    @Test
    public void shouldIncrementAllCountersAndResetThem() {
        assertThat(Statistics.getError404s(), is(equalTo(0L)));
        assertThat(Statistics.getError500s(), is(equalTo(0L)));
        assertThat(Statistics.getRequestsHandled(), is(equalTo(0L)));

        Statistics.incrementError404();
        assertThat(Statistics.getError404s(), is(equalTo(1L)));
        Statistics.incrementError500();
        assertThat(Statistics.getError500s(), is(equalTo(1L)));
        Statistics.incrementRequestHandled();
        assertThat(Statistics.getRequestsHandled(), is(equalTo(1L)));

        Statistics.incrementError404();
        assertThat(Statistics.getError404s(), is(equalTo(2L)));
        Statistics.incrementError500();
        assertThat(Statistics.getError500s(), is(equalTo(2L)));
        Statistics.incrementRequestHandled();
        assertThat(Statistics.getRequestsHandled(), is(equalTo(2L)));

        Statistics.reset();
        assertThat(Statistics.getError404s(), is(equalTo(0L)));
        assertThat(Statistics.getError500s(), is(equalTo(0L)));
        assertThat(Statistics.getRequestsHandled(), is(equalTo(0L)));
    }

    @Test
    public void shouldIncrementAllByteCounters() {
        assertThat(Statistics.getBytesReceived(), is(equalTo(0L)));
        assertThat(Statistics.getBytesSent(), is(equalTo(0L)));

        Statistics.addBytesReceived(3);
        assertThat(Statistics.getBytesReceived(), is(equalTo(3L)));
        Statistics.addBytesReceived(5);
        assertThat(Statistics.getBytesReceived(), is(equalTo(8L)));

        Statistics.addBytesSent(2);
        assertThat(Statistics.getBytesSent(), is(equalTo(2L)));
        Statistics.addBytesSent(11);
        assertThat(Statistics.getBytesSent(), is(equalTo(13L)));

        Statistics.reset();
        assertThat(Statistics.getBytesReceived(), is(equalTo(0L)));
        assertThat(Statistics.getBytesSent(), is(equalTo(0L)));
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
