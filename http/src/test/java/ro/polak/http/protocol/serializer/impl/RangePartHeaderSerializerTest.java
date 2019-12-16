package ro.polak.http.protocol.serializer.impl;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import ro.polak.http.RangePartHeader;
import ro.polak.http.servlet.Range;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class RangePartHeaderSerializerTest {

    private final RangePartHeaderSerializer rangePartHeaderSerializer = new RangePartHeaderSerializer();

    @Test
    public void shouldSerializeRangeProperly() {
        Range range = new Range(333L, 1234L);
        String boundary = "BBBOOUUNNNDDAARRYY";
        String contentType = "application/pdf";
        long totalLength = 12345L;
        RangePartHeader rangePartHeader = new RangePartHeader(range, boundary, contentType, totalLength);

        String serialized = rangePartHeaderSerializer.serialize(rangePartHeader);
        assertThat(serialized, startsWith("--" + boundary + "\r\n"));
        assertThat(serialized, endsWith("\r\n\r\n"));
        assertThat(serialized, containsString("\r\nContent-Type: " + contentType + "\r\n"));
        assertThat(serialized, containsString("\r\nContent-Range: bytes 333-1234/12345\r\n"));
    }

    @Test
    public void shouldReturnLengthForASingeRange() {
        Range range = new Range(333L, 1234L);
        String boundary = "BBBOOUUNNNDDAARRYY";
        String contentType = "application/pdf";
        long totalLength = 12345L;
        long computedLength = rangePartHeaderSerializer
                .getPartHeadersLength(Collections.singletonList(range), boundary, contentType, totalLength);

        assertThat(computedLength, is(0L));
    }

    @Test
    public void shouldReturnLengthForMultipleRanges() {
        Range range = new Range(333L, 1234L);
        String boundary = "BBBOOUUNNNDDAARRYY";
        String contentType = "application/pdf";
        long totalLength = 12345L;

        RangePartHeader rangePartHeader = new RangePartHeader(range, boundary, contentType, totalLength);
        String serialized = rangePartHeaderSerializer.serialize(rangePartHeader);

        long serializedLength = serialized.length();
        long computedLength = rangePartHeaderSerializer
                .getPartHeadersLength(Arrays.asList(range, range), boundary, contentType, totalLength);
        long firstLineLength = "\r\n".length();
        long lastLineLength = ("\r\n--" + boundary + "--\r\n\r\n").length();
        assertThat(computedLength, is(firstLineLength + 2 * serializedLength + lastLineLength));
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
