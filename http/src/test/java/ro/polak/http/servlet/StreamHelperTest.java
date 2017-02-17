package ro.polak.http.servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.ArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ro.polak.http.protocol.parser.RangeHelper;
import ro.polak.http.protocol.parser.impl.Range;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;


public class StreamHelperTest {

    public static final String BOUNDARY = "";
    public static final String CONTENT_TYPE = "application/pdf";
    public static final int TOTAL_LENGTH = 0;
    private ByteArrayInputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private byte[] inputBytes;
    private final StreamHelper streamHelper = new StreamHelper();
    private final RangeHelper rangeHelper = new RangeHelper();

    @Before
    public void setup() {
        inputBytes = new byte[1024 * 5];
        new Random().nextBytes(inputBytes);
        inputStream = new ByteArrayInputStream(inputBytes);
        outputStream = new ByteArrayOutputStream();
    }

    @Test
    public void shouldServeTheSameBytes() throws IOException {
        streamHelper.serveStream(inputStream, outputStream);
        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(greaterThan(0)));
        assertThat(out, new ArrayEquals(inputBytes));
    }

    @Test
    public void shouldServeTheSameBytesForSingeRangeSmallerThanTheBuffer() throws IOException {
        Range range = new Range(3, 200);

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, Arrays.asList(range));

        streamHelper.serveStream(inputStream, outputStream, range);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) rangeHelper.getTotalLength(Arrays.asList(range)))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForSingeRangeGreaterThanTheBuffer() throws IOException {
        Range range = new Range(3, 1024);

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, Arrays.asList(range));

        streamHelper.serveStream(inputStream, outputStream, range);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) rangeHelper.getTotalLength(Arrays.asList(range)))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForMultipleRangeSmallerThanTheBuffer() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 2));
        ranges.add(new Range(2, 2));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges, BOUNDARY, CONTENT_TYPE, 0);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) rangeHelper.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForMultipleRangeSmallerThanTheBufferOverlapping() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 49));
        ranges.add(new Range(40, 59));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges, BOUNDARY, CONTENT_TYPE, TOTAL_LENGTH);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) rangeHelper.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForMultipleRangeGreaterThanTheBuffer() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 550));
        ranges.add(new Range(1024, 1623));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges, BOUNDARY, CONTENT_TYPE, TOTAL_LENGTH);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) rangeHelper.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void selfTest() {
        byte[] sample = {0, 1, 2, 3, 4};
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 1));
        ranges.add(new Range(2, 4));
        assertThat(getSliceForRanges(sample, ranges), new ArrayEquals(sample));
    }

    private byte[] getSliceForRanges(byte[] input, List<Range> ranges) {
        byte[] output = new byte[(int) rangeHelper.getTotalLength(ranges)];

        int destPos = 0;
        for (Range range : ranges) {
            byte[] slice = Arrays.copyOfRange(input, (int) range.getFrom(), (int) range.getTo()+1);
            System.arraycopy(slice, 0, output, destPos, slice.length);
            destPos += slice.length;
        }

        return output;
    }
}