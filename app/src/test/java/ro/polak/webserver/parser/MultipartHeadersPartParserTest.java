package ro.polak.webserver.parser;

import org.junit.BeforeClass;
import org.junit.Test;

import ro.polak.webserver.MultipartHeadersPart;
import ro.polak.webserver.parser.MultipartHeadersPartParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class MultipartHeadersPartParserTest {

    private static MultipartHeadersPartParser multipartHeadersPartParser;

    @BeforeClass
    public static void setup() {
        multipartHeadersPartParser = new MultipartHeadersPartParser();
    }

    @Test
    public void shouldParseValidAttachmentHeader() {
        MultipartHeadersPart headers = multipartHeadersPartParser.parse("Content-Disposition: attachment; name=\"FIELDNAME\"; filename=\"FILE.PDF\"\nContent-type: application/pdf");

        assertThat(headers.getFileName(), is("FILE.PDF"));
        assertThat(headers.getName(), is("FIELDNAME"));
        assertThat(headers.getContentType(), is("application/pdf"));
    }

    @Test
    public void shouldParseValidAttachmentHeaderCaseInsensitive() {
        MultipartHeadersPart headers = multipartHeadersPartParser.parse("CONTENT-DISPOSITION: attachment; NAME=\"FIELDNAME\"; FILENAME=\"FILE.PDF\"\nContent-TYPE: application/pdf");

        assertThat(headers.getFileName(), is("FILE.PDF"));
        assertThat(headers.getName(), is("FIELDNAME"));
        assertThat(headers.getContentType(), is("application/pdf"));
    }

    @Test
    public void shouldParseFormDataText() {
        MultipartHeadersPart headers = multipartHeadersPartParser.parse("Content-Disposition: form-data; name=\"text\"");

        assertThat(headers.getFileName(), is(nullValue()));
        assertThat(headers.getName(), is("text"));
        assertThat(headers.getContentType(), is(nullValue()));
    }
}
