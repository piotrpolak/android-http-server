package ro.polak.webserver;

import org.junit.Test;

import java.util.Hashtable;

import static org.junit.Assert.*;

public class QueryStringParserTest {

    @Test
    public void testDefault() {

        String data = "&param1=ABCD1"
                + "&param2=ABCD2"
                + "&param3=ABC=DEF"
                + "&param4=A%20B%20%3D%20%25%20*";

        QueryStringParser parser = new QueryStringParser();
        Hashtable<String, String> parameters = parser.parse(data);

        assertEquals(4, parameters.size());

        assertEquals("ABCD1", parameters.get("param1"));
        assertEquals("ABCD2", parameters.get("param2"));
        assertEquals("ABC=DEF", parameters.get("param3"));
        assertEquals("A B = % *", parameters.get("param4"));
    }
}