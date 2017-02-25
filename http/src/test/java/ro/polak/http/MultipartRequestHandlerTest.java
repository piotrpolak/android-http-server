package ro.polak.http;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import ro.polak.http.protocol.exception.PayloadTooLargeProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.servlet.UploadedFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class MultipartRequestHandlerTest {

    private static final String BOUNDARY = "------------BOUNDARY";
    private static final String NEW_LINE = "\r\n";
    private static final String temporaryUploadsDirectory
            = System.getProperty("java.io.tmpdir") + "/";

    @Test
    public void shouldParseBasicFields() throws MalformedInputException {
        String data =
                "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_1\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "A123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_2\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "B123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_3\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "C123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length(), BOUNDARY, temporaryUploadsDirectory);

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
        String data =
                "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_0\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "--------------BOUNDAR" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_1\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "------------BOUNDARY" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_2\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "------------BOUNDARY-" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length(), BOUNDARY, temporaryUploadsDirectory);

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
        String data =
                "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_1\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "A123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: attachment; name=\"FIELDNAME\"; " +
                        "filename=\"FILE.PDF\"" +
                        NEW_LINE +
                        "Content-type: application/pdf" +
                        NEW_LINE +
                        NEW_LINE +
                        "ABCD" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length(), BOUNDARY, temporaryUploadsDirectory);
        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().size(), is(1));

        assertThat(mrh.getPost().get("field_1"), is("A123"));
        assertThat(mrh.getUploadedFiles().size(), is(1));
        assertThat(mrh.getUploadedFiles().iterator().next().getFile().length(), is(4l));
        // TODO Check file content
    }

    @Test
    public void shouldHandleFileUploadForEmptyFile() throws MalformedInputException {
        String data =
                "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_1\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "A123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: attachment; name=\"FIELDNAME_00\"; " +
                        "filename=\"FILE.PDF\"" +
                        NEW_LINE +
                        "Content-type: application/pdf" +
                        NEW_LINE +
                        NEW_LINE +
                        "" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: attachment; name=\"FIELDNAME_01\"; " +
                        "filename=\"FILE.PDF\"" +
                        NEW_LINE +
                        "Content-type: application/pdf" +
                        NEW_LINE +
                        NEW_LINE +
                        "ABCD" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length(), BOUNDARY, temporaryUploadsDirectory);
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
        assertThat(uploadedFiles.next().getFile().length(), is(4l));
        // TODO Check file content
    }

    @Test
    public void shouldHandleFieldsOnBufferMargin() throws MalformedInputException {
        // TODO Implement similar test for file upload to see whether delayed write works fine
        String shortBoundary = "%";
        String data =
                "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_00\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_01\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "A" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_02\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "BB" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_03\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "CCC" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_04\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "DDDD" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_05\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "EEEEE" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_06\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "FFFFFF" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_07\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "GGGGGGG" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_08\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "HHHHHHHH" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_09\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "IIIIIIIII" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_10\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "JJJJJJJJJJ" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_11\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "KKKKKKKKKKK" +
                        NEW_LINE +
                        "--" + shortBoundary +
                        NEW_LINE +
                        NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length(), shortBoundary, temporaryUploadsDirectory, 1);
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
        String data =
                "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_1\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "A123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_2\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "B123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        "Content-Disposition: form-data; name=\"field_3\"" +
                        NEW_LINE +
                        NEW_LINE +
                        "C123" +
                        NEW_LINE +
                        "--" + BOUNDARY +
                        NEW_LINE +
                        NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length(), BOUNDARY, temporaryUploadsDirectory);

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
        String data =
                "--123";

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                data.length() * 2, BOUNDARY, temporaryUploadsDirectory);

        mrh.handle();
    }

    @Test(expected = PayloadTooLargeProtocolException.class)
    public void shouldStopParsingOnWrongContentLengthInBeforeBoundary()
            throws MalformedInputException {
        String data = "----------------------------------------------------------" + BOUNDARY +
                NEW_LINE +
                "Content-Disposition: form-data; name=\"field_1\"" +
                NEW_LINE +
                NEW_LINE +
                "A123" +
                NEW_LINE +
                "--" + BOUNDARY +
                NEW_LINE +
                NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), 5,
                BOUNDARY, temporaryUploadsDirectory);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }


    @Test(expected = PayloadTooLargeProtocolException.class)
    public void shouldStopParsingOnWrongContentLengthInBody() throws MalformedInputException {
        String begin = "--" + BOUNDARY +
                NEW_LINE +
                "Content-Disposition: form-data; name=\"field_1\"" +
                NEW_LINE +
                NEW_LINE +
                "A123" +
                NEW_LINE;

        String data = begin +
                "--" + BOUNDARY +
                NEW_LINE +
                "Content-Disposition: form-data; name=\"field_2\"" +
                NEW_LINE +
                NEW_LINE +
                "B123" + NEW_LINE +
                "--" + BOUNDARY + NEW_LINE +
                "Content-Disposition: form-data; name=\"field_3\"" +
                NEW_LINE +
                NEW_LINE +
                "C123" +
                NEW_LINE +
                "--" + BOUNDARY +
                NEW_LINE +
                NEW_LINE;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data),
                begin.length(), BOUNDARY, temporaryUploadsDirectory);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }

    private InputStream getStreamOutOfString(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
}
