package ro.polak.http.protocol.parser.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.servlet.Range;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void shouldThrowExceptionOnMissingLength() {
        final RangeParser rangeParser = new RangeParser();

        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                rangeParser.parse("bytes=100-");
            }
        });
    }

    @Test
    public void shouldThrowExceptionOnMissingValues() {
        final RangeParser rangeParser = new RangeParser();
        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                rangeParser.parse("bytes=");
            }
        });
    }

    @Test
    public void shouldThrowExceptionWhenMissingFromValue() {
        final RangeParser rangeParser = new RangeParser();
        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                rangeParser.parse("bytes=-200");
            }
        });
    }

    @Test
    public void shouldThrowExceptionNonParsableValues() {
        final RangeParser rangeParser = new RangeParser();
        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                rangeParser.parse("bytes=1-a");
            }
        });
    }

    @Test
    public void shouldThrowExceptionOnIncompleteRange() {
        final RangeParser rangeParser = new RangeParser();
        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                rangeParser.parse("bytes=1");
            }
        });
    }

    @Test
    public void shouldThrowExceptionOnUnrecognizedUnit() {
        final RangeParser rangeParser = new RangeParser();
        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                rangeParser.parse("octets=1-100");
            }
        });
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
