package ro.polak.webserver;

import org.junit.Test;

import static org.junit.Assert.*;

public class MultipartHeadersPartTest {

    @Test
    public void testParseFile() {
        MultipartHeadersPart headers = new MultipartHeadersPart();
        headers.parse("Content-Disposition: attachment; name=\"FIELDNAME\"; filename=\"FILE.PDF\"\nContent-type: application/pdf");

        assertEquals("FILE.PDF", headers.getFileName());
        assertEquals("FIELDNAME", headers.getPostFieldName());
        assertEquals("application/pdf", headers.getContentType());
    }
}
