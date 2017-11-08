package ro.polak.http;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static ro.polak.http.ExtraMarchers.utilityClass;

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
        assertThat(Statistics.getError404s(), is(equalTo(0l)));
        assertThat(Statistics.getError500s(), is(equalTo(0l)));
        assertThat(Statistics.getRequestsHandled(), is(equalTo(0l)));

        Statistics.incrementError404();
        assertThat(Statistics.getError404s(), is(equalTo(1l)));
        Statistics.incrementError500();
        assertThat(Statistics.getError500s(), is(equalTo(1l)));
        Statistics.incrementRequestHandled();
        assertThat(Statistics.getRequestsHandled(), is(equalTo(1l)));

        Statistics.incrementError404();
        assertThat(Statistics.getError404s(), is(equalTo(2l)));
        Statistics.incrementError500();
        assertThat(Statistics.getError500s(), is(equalTo(2l)));
        Statistics.incrementRequestHandled();
        assertThat(Statistics.getRequestsHandled(), is(equalTo(2l)));

        Statistics.reset();
        assertThat(Statistics.getError404s(), is(equalTo(0l)));
        assertThat(Statistics.getError500s(), is(equalTo(0l)));
        assertThat(Statistics.getRequestsHandled(), is(equalTo(0l)));
    }

    @Test
    public void shouldIncrementAllByteCounters() {
        assertThat(Statistics.getBytesReceived(), is(equalTo(0l)));
        assertThat(Statistics.getBytesSent(), is(equalTo(0l)));

        Statistics.addBytesReceived(3);
        assertThat(Statistics.getBytesReceived(), is(equalTo(3l)));
        Statistics.addBytesReceived(5);
        assertThat(Statistics.getBytesReceived(), is(equalTo(8l)));

        Statistics.addBytesSent(2);
        assertThat(Statistics.getBytesSent(), is(equalTo(2l)));
        Statistics.addBytesSent(11);
        assertThat(Statistics.getBytesSent(), is(equalTo(13l)));

        Statistics.reset();
        assertThat(Statistics.getBytesReceived(), is(equalTo(0l)));
        assertThat(Statistics.getBytesSent(), is(equalTo(0l)));
    }

}