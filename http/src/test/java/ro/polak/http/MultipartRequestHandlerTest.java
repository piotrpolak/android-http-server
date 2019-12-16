package ro.polak.http;

import org.junit.Test;
import ro.polak.http.exception.protocol.PayloadTooLargeProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.protocol.parser.impl.HeadersParser;
import ro.polak.http.protocol.parser.impl.MultipartHeadersPartParser;
import ro.polak.http.servlet.UploadedFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class MultipartRequestHandlerTest {

    private static final String BOUNDARY = "------------BOUNDARY";
    private static final String TEMPORARY_UPLOADS_DIRECTORY
            = System.getProperty("java.io.tmpdir") + "/";
    private static final Parser<MultipartHeadersPart> PARSER = new MultipartHeadersPartParser(
            new HeadersParser()
    );

    @Test
    public void shouldParseBasicFields() throws MalformedInputException {
        String data = new MultipartInputBuilder(BOUNDARY)
                .withField("field_1", "A123")
                .withField("field_2", "B123")
                .withField("field_3", "C123")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length(), BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().size(), is(3));

        assertThat(mrh.getPost().get("field_1"), is("A123"));
        assertThat(mrh.getPost().get("field_2"), is("B123"));
        assertThat(mrh.getPost().get("field_3"), is("C123"));
    }

    @Test
    public void shouldParseWhenBoundaryIsLikeFieldValues() throws MalformedInputException {
        String data = new MultipartInputBuilder(BOUNDARY)
                .withField("field_0", "--------------BOUNDAR")
                .withField("field_1", "------------BOUNDARY")
                .withField("field_2", "------------BOUNDARY-")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length(), BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }


        assertThat(mrh.getPost().size(), is(3));

        assertThat(mrh.getPost().get("field_0"), is("--------------BOUNDAR"));
        assertThat(mrh.getPost().get("field_1"), is("------------BOUNDARY"));
        assertThat(mrh.getPost().get("field_2"), is("------------BOUNDARY-"));
    }

    @Test
    public void shouldHandleBasicFileUpload() throws MalformedInputException {
        String data = new MultipartInputBuilder(BOUNDARY)
                .withField("field_1", "A123")
                .withFile("FIELDNAME", "FILE.PDF", "application/pdf", "ABCD")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length(), BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);
        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().size(), is(1));

        assertThat(mrh.getPost().get("field_1"), is("A123"));
        assertThat(mrh.getUploadedFiles().size(), is(1));
        assertThat(mrh.getUploadedFiles().iterator().next().getFile().length(), is(4L));
        // TODO Check file content
    }

    @Test
    public void shouldHandleFileUploadForEmptyFile() throws MalformedInputException {
        String data = new MultipartInputBuilder(BOUNDARY)
                .withField("field_1", "A123")
                .withFile("FIELDNAME_00", "FILE.PDF", "application/pdf", "")
                .withFile("FIELDNAME_01", "FILE.PDF", "application/pdf", "ABCD")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length(), BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);
        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().size(), is(1));

        assertThat(mrh.getPost().get("field_1"), is("A123"));
        assertThat(mrh.getUploadedFiles().size(), is(2));

        Iterator<UploadedFile> uploadedFiles = mrh.getUploadedFiles().iterator();

        assertThat(uploadedFiles.next().getFile().length(), is(0L));
        assertThat(uploadedFiles.next().getFile().length(), is(4L));
        // TODO Check file content
    }

    @Test
    public void shouldHandleFieldsOnBufferMargin() throws MalformedInputException {
        // TODO Implement similar test for file upload to see whether delayed write works fine
        String shortBoundary = "%";

        String data = new MultipartInputBuilder(shortBoundary)
                .withField("field_00", "")
                .withField("field_01", "A")
                .withField("field_02", "BB")
                .withField("field_03", "CCC")
                .withField("field_04", "DDDD")
                .withField("field_05", "EEEEE")
                .withField("field_06", "FFFFFF")
                .withField("field_07", "GGGGGGG")
                .withField("field_08", "HHHHHHHH")
                .withField("field_09", "IIIIIIIII")
                .withField("field_10", "JJJJJJJJJJ")
                .withField("field_11", "KKKKKKKKKKK")
                .build();


        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length(), shortBoundary, TEMPORARY_UPLOADS_DIRECTORY, 1);
        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().get("field_00"), is(""));
        assertThat(mrh.getPost().get("field_01"), is("A"));
        assertThat(mrh.getPost().get("field_02"), is("BB"));
        assertThat(mrh.getPost().get("field_03"), is("CCC"));
        assertThat(mrh.getPost().get("field_04"), is("DDDD"));
        assertThat(mrh.getPost().get("field_05"), is("EEEEE"));
        assertThat(mrh.getPost().get("field_06"), is("FFFFFF"));
        assertThat(mrh.getPost().get("field_07"), is("GGGGGGG"));
        assertThat(mrh.getPost().get("field_08"), is("HHHHHHHH"));
        assertThat(mrh.getPost().get("field_09"), is("IIIIIIIII"));
        assertThat(mrh.getPost().get("field_10"), is("JJJJJJJJJJ"));
        assertThat(mrh.getPost().get("field_11"), is("KKKKKKKKKKK"));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldBeHandledOnceOnly() throws MalformedInputException {
        String data = new MultipartInputBuilder(BOUNDARY)
                .withField("field_1", "A123")
                .withField("field_2", "B123")
                .withField("field_3", "C123")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length(), BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);

        try {
            mrh.handle();
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionOnEndedBoundary()
            throws MalformedInputException, IOException {
        String data = "--123";

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                data.length() * 2, BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);

        mrh.handle();
    }

    @Test(expected = PayloadTooLargeProtocolException.class)
    public void shouldStopParsingOnWrongContentLengthInBeforeBoundary()
            throws MalformedInputException {

        String data = "--------------------------------------------------------"
                + new MultipartInputBuilder(BOUNDARY)
                .withField("field_1", "A123")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data), 5,
                BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }


    @Test(expected = PayloadTooLargeProtocolException.class)
    public void shouldStopParsingOnWrongContentLengthInBody() throws MalformedInputException {
        String begin = new MultipartInputBuilder(BOUNDARY)
                .withField("field_1", "A123")
                .build();

        String data = begin + new MultipartInputBuilder(BOUNDARY)
                .withField("field_2", "B123")
                .withField("field_3", "C123")
                .build();

        MultipartRequestHandler mrh = new MultipartRequestHandler(PARSER, getStreamOutOfString(data),
                begin.length(), BOUNDARY, TEMPORARY_UPLOADS_DIRECTORY, 2048);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }

    private InputStream getStreamOutOfString(final String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Builds multipart input.
     */
    private static final class MultipartInputBuilder {

        private static final String NEW_LINE = "\r\n";
        private StringBuilder contents = new StringBuilder();
        private String boundary;

        private MultipartInputBuilder(final String boundary) {
            this.boundary = boundary;

            addBoundary();
        }

        private void addBoundary() {
            contents.append("--").append(boundary).append(NEW_LINE);
        }

        public MultipartInputBuilder withField(final String fieldName, final String fieldValue) {
            contents.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"")
                    .append(NEW_LINE)
                    .append(NEW_LINE)
                    .append(fieldValue)
                    .append(NEW_LINE);
            addBoundary();

            return this;
        }

        public MultipartInputBuilder withFile(final String fieldName, final String fileName,
                                              final String contentType, final String fileContents) {
            contents.append("Content-Disposition: attachment; name=\"").append(fieldName).append("\"; ")
                    .append("filename=\"").append(fileName).append("\"")
                    .append(NEW_LINE)
                    .append("Content-type: ").append(contentType)
                    .append(NEW_LINE)
                    .append(NEW_LINE)
                    .append(fileContents)
                    .append(NEW_LINE);
            addBoundary();

            return this;
        }

        public String build() {
            contents.append(NEW_LINE);
            return contents.toString();
        }
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
