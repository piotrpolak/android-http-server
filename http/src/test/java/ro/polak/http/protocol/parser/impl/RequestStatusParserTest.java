package ro.polak.http.protocol.parser.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ro.polak.http.RequestStatus;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// CHECKSTYLE.OFF: JavadocType
public class RequestStatusParserTest {

    @Test
    public void shouldParseStatusString() throws MalformedInputException {
        Parser<RequestStatus> requestStatusParser = new RequestStatusParser();
        RequestStatus requestStatus = requestStatusParser.parse("GET /home?param1=ABC&param2=123 HTTP/1.1");

        assertThat(requestStatus.getMethod(), is("GET"));
        assertThat(requestStatus.getQueryString(), is("param1=ABC&param2=123"));
        assertThat(requestStatus.getUri(), is("/home"));
        assertThat(requestStatus.getProtocol(), is("HTTP/1.1"));
    }

    @Test
    public void shouldIgnoreTrailingCharacters() throws MalformedInputException {
        Parser<RequestStatus> requestStatusParser = new RequestStatusParser();
        RequestStatus requestStatus = requestStatusParser.parse("GET /home?param1=ABC&param2=123 HTTP/1.1\r\n");

        assertThat(requestStatus.getMethod(), is("GET"));
        assertThat(requestStatus.getQueryString(), is("param1=ABC&param2=123"));
        assertThat(requestStatus.getUri(), is("/home"));
        assertThat(requestStatus.getProtocol(), is("HTTP/1.1"));
    }

    @Test
    public void shouldThrowMalformedInputExceptionOnInvalidStatus() {
        final Parser<RequestStatus> requestStatusParser = new RequestStatusParser();
        assertThrows(MalformedInputException.class, new Executable() {
            @Override
            public void execute() throws MalformedInputException {
                requestStatusParser.parse("GET HTTP/1.1");
            }
        });
    }
}
// CHECKSTYLE.ON: JavadocType
