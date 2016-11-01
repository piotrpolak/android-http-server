package ro.polak.webserver;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QueryStringParserTest {

    @Test
    public void shouldParseFields() {

        String data = "&param1=ABCD1"
                + "&param2=ABCD2"
                + "&param3=ABC=DEF"
                + "&param4=A%20B%20%3D%20%25%20*";

        QueryStringParser parser = new QueryStringParser();
        Map<String, String> parameters = parser.parse(data);

        assertThat(parameters.size(), is(4));

        assertThat(parameters.get("param1"), is("ABCD1"));
        assertThat(parameters.get("param2"), is("ABCD2"));
        assertThat(parameters.get("param3"), is("ABC=DEF"));
        assertThat(parameters.get("param4"), is("A B = % *"));
    }
}