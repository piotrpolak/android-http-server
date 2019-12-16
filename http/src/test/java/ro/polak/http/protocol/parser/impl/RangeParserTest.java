package ro.polak.http.protocol.parser.impl;

import org.junit.Test;

import java.util.List;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.servlet.Range;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class RangeParserTest {

    @Test
    public void shouldParseBasicValue() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        List<Range> rageList = rangeParser.parse("bytes=1-100");
        assertThat(rageList.size(), is(1));
        assertThat(rageList.get(0).getFrom(), is(1L));
        assertThat(rageList.get(0).getTo(), is(100L));
    }

    @Test
    public void shouldParseMultipleValues() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        List<Range> rageList = rangeParser.parse("bytes=1-100,200-301");
        assertThat(rageList.size(), is(2));
        assertThat(rageList.get(0).getFrom(), is(1L));
        assertThat(rageList.get(0).getTo(), is(100L));
        assertThat(rageList.get(1).getFrom(), is(200L));
        assertThat(rageList.get(1).getTo(), is(301L));
    }

    @Test
    public void shouldParseMultipleValuesSpaceSeparated() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        List<Range> rageList = rangeParser.parse("bytes=1-100, 200-301");
        assertThat(rageList.size(), is(2));
        assertThat(rageList.get(0).getFrom(), is(1L));
        assertThat(rageList.get(0).getTo(), is(100L));
        assertThat(rageList.get(1).getFrom(), is(200L));
        assertThat(rageList.get(1).getTo(), is(301L));
    }

    @Test(expected = MalformedInputException.class)
    public void shouldThrowExceptionOnMissingLength() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        rangeParser.parse("bytes=100-");
    }

    @Test(expected = MalformedInputException.class)
    public void shouldThrowExceptionOnMissingValues() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        rangeParser.parse("bytes=");
    }

    @Test(expected = MalformedInputException.class)
    public void shouldThrowExceptionWhenMissingFromValue() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        rangeParser.parse("bytes=-200");
    }

    @Test(expected = MalformedInputException.class)
    public void shouldThrowExceptionNonParsableValues() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        rangeParser.parse("bytes=1-a");
    }

    @Test(expected = MalformedInputException.class)
    public void shouldThrowExceptionOnIncompleteRange() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        rangeParser.parse("bytes=1");
    }

    @Test(expected = MalformedInputException.class)
    public void shouldThrowExceptionOnUnrecognizedUnit() throws MalformedInputException {
        RangeParser rangeParser = new RangeParser();
        rangeParser.parse("octets=1-100");
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
