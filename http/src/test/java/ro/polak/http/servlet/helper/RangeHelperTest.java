package ro.polak.http.servlet.helper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ro.polak.http.servlet.Range;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class RangeHelperTest {

    private RangeHelper rangeHelper = new RangeHelper();

    @Test
    public void shouldComputeLength() {
        assertThat(rangeHelper.getRangeLength(new Range(0, 0)), is(1L));
        assertThat(rangeHelper.getRangeLength(new Range(0, 10)), is(11L));
        assertThat(rangeHelper.getRangeLength(new Range(2, 3)), is(2L));
        assertThat(rangeHelper.getRangeLength(new Range(11, 12)), is(2L));
    }

    @Test
    public void shouldComputeTotalLength() {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 0));
        ranges.add(new Range(0, 10));
        assertThat(rangeHelper.getTotalLength(ranges), is(12L));
    }

    @Test
    public void shouldComputeTotalLengthForLargerValues() {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 550));
        ranges.add(new Range(1024, 1623));
        assertThat(rangeHelper.getTotalLength(ranges), is(1151L));
    }

    @Test
    public void shouldComputeTotalLengthX() {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 1));
        ranges.add(new Range(2, 3));
        assertThat(rangeHelper.getTotalLength(ranges), is(4L));
    }

    @Test
    public void shouldBeValid() {
        assertThat(rangeHelper.isRangeValid(new Range(0, 0)), is(true));
        assertThat(rangeHelper.isRangeValid(new Range(10, 20)), is(true));
        assertThat(rangeHelper.isRangeValid(new Range(10, 10)), is(true));
    }

    @Test
    public void shouldNotBeValid() {
        assertThat(rangeHelper.isRangeValid(new Range(10, 6)), is(false));
        assertThat(rangeHelper.isRangeValid(new Range(-1, 6)), is(false));
    }


    @Test
    public void shouldBeSatisfiable() {
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(0, 0)), 1), is(true));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(0, 1)), 2), is(true));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(0, 2)), 3), is(true));
    }

    @Test
    public void shouldNotBeSatisfiable() {
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(0, 0)), 0), is(false));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(0, 1)), 1), is(false));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(0, 2)), 1), is(false));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(-1, 0)), 0), is(false));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(-1, 0)), 55), is(false));
        assertThat(rangeHelper.isSatisfiable(Collections.singletonList(new Range(50, 49)), 0), is(false));
    }

    @Test
    public void shouldNotBeSatisfiableWhenFirstElementIsFine() {
        assertThat(rangeHelper.isSatisfiable(Arrays.asList(new Range(0, 0), new Range(-1, 0)), 5), is(false));
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
