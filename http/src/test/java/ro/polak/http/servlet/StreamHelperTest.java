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

import ro.polak.http.protocol.parser.impl.Range;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;


public class StreamHelperTest {

    private ByteArrayInputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private byte[] inputBytes;
    private final StreamHelper streamHelper = new StreamHelper();

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
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(3, 200));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) Range.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForSingeRangeGreaterThanTheBuffer() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(3, 1024));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) Range.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForMultipleRangeSmallerThanTheBuffer() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 2));
        ranges.add(new Range(2, 2));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) Range.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForMultipleRangeSmallerThanTheBufferOverlapping() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 50));
        ranges.add(new Range(40, 20));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) Range.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void shouldServeTheSameBytesForMultipleRangeGreaterThanTheBuffer() throws IOException {
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 550));
        ranges.add(new Range(1024, 600));

        byte[] inputBytesSliced = getSliceForRanges(inputBytes, ranges);

        streamHelper.serveStream(inputStream, outputStream, ranges);

        byte[] out = outputStream.toByteArray();
        assertThat(out.length, is(equalTo((int) Range.getTotalLength(ranges))));

        assertThat(out, new ArrayEquals(inputBytesSliced));
    }

    @Test
    public void selfTest() {
        byte[] sample = {0, 1, 2, 3};
        List<Range> ranges = new ArrayList<>();
        ranges.add(new Range(0, 2));
        ranges.add(new Range(2, 2));
        assertThat(getSliceForRanges(sample, ranges), new ArrayEquals(sample));
    }

    private byte[] getSliceForRanges(byte[] input, List<Range> ranges) {
        byte[] output = new byte[(int) Range.getTotalLength(ranges)];

        int destPos = 0;
        for (Range range : ranges) {
            byte[] slice = Arrays.copyOfRange(input, (int) range.getFrom(), (int) (range.getFrom() + range.getLength()));
            System.arraycopy(slice, 0, output, destPos, slice.length);
            destPos += slice.length;
        }

        return output;
    }
}