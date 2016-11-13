package ro.polak.webserver.protocol.parser.impl;

import org.junit.Test;

import ro.polak.webserver.RequestStatus;
import ro.polak.webserver.protocol.parser.Parser;
import ro.polak.webserver.protocol.parser.impl.RequestStatusParser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RequestStatusParserTest {

    @Test
    public void shouldParseStatusString() {
        Parser<RequestStatus> requestStatusParser = new RequestStatusParser();
        RequestStatus requestStatus = requestStatusParser.parse("GET /home?param1=ABC&param2=123 HTTP/1.1");

        assertThat(requestStatus.getMethod(), is("GET"));
        assertThat(requestStatus.getQueryString(), is("param1=ABC&param2=123"));
        assertThat(requestStatus.getUri(), is("/home"));
        assertThat(requestStatus.getProtocol(), is("HTTP/1.1"));
    }
}