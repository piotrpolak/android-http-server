package ro.polak.http;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import ro.polak.http.protocol.parser.MalformedInputException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

public class MultipartRequestHandlerTest {

    private String boundary;
    private String nl;
    private String temporaryUploadsDirectory = System.getProperty("java.io.tmpdir") + "/";

    @Before
    public void setUp() {
        boundary = "------------BOUNDARY";
        nl = "\r\n";
    }

    @Test
    public void shouldParseBasicFields() throws MalformedInputException {
        String data =
                "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_1\"" + nl +
                        nl +
                        "A123" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_2\"" + nl +
                        nl +
                        "B123" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_3\"" + nl +
                        nl +
                        "C123" + nl +
                        "--" + boundary + nl + nl;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), data.length(), boundary, temporaryUploadsDirectory);

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
                "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_0\"" + nl +
                        nl +
                        "--------------BOUNDAR" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_1\"" + nl +
                        nl +
                        "------------BOUNDARY" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_2\"" + nl +
                        nl +
                        "------------BOUNDARY-" + nl +
                        "--" + boundary + nl + nl;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), data.length(), boundary, temporaryUploadsDirectory);

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
                "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_1\"" + nl +
                        nl +
                        "A123" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: attachment; name=\"FIELDNAME\"; filename=\"FILE.PDF\"\n" +
                        "Content-type: application/pdf" + nl +
                        nl +
                        "ABCD" + nl +
                        "--" + boundary + nl + nl;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), data.length(), boundary, temporaryUploadsDirectory);
        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().size(), is(1));

        assertThat(mrh.getPost().get("field_1"), is("A123"));
        assertThat(mrh.getUploadedFiles().size(), is(1));
        assertThat(mrh.getUploadedFiles().get(0).getFile().length(), is(4l));
        // TODO Check file content
    }

    @Test
    public void shouldHandleFieldsOnBufferMargin() throws MalformedInputException {
        // TODO Implement similar test for file upload to see whether delayed write works fine
        boundary = "%";
        String data =
                "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_01\"" + nl +
                        nl +
                        "A" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_02\"" + nl +
                        nl +
                        "BB" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_03\"" + nl +
                        nl +
                        "CCC" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_04\"" + nl +
                        nl +
                        "DDDD" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_05\"" + nl +
                        nl +
                        "EEEEE" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_06\"" + nl +
                        nl +
                        "FFFFFF" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_07\"" + nl +
                        nl +
                        "GGGGGGG" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_08\"" + nl +
                        nl +
                        "HHHHHHHH" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_09\"" + nl +
                        nl +
                        "IIIIIIIII" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_10\"" + nl +
                        nl +
                        "JJJJJJJJJJ" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_11\"" + nl +
                        nl +
                        "KKKKKKKKKKK" + nl +
                        "--" + boundary + nl + nl;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), data.length(), boundary, temporaryUploadsDirectory, 1);
        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

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
                "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_1\"" + nl +
                        nl +
                        "A123" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_2\"" + nl +
                        nl +
                        "B123" + nl +
                        "--" + boundary + nl +
                        "Content-Disposition: form-data; name=\"field_3\"" + nl +
                        nl +
                        "C123" + nl +
                        "--" + boundary + nl + nl;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), data.length(), boundary, temporaryUploadsDirectory);

        try {
            mrh.handle();
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }

    @Test(expected = IOException.class)
    public void shouldThrowIOExceptionOnEndedBoundary() throws MalformedInputException, IOException {
        String data =
                "--123";

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), data.length() * 2, boundary, temporaryUploadsDirectory);

        mrh.handle();
    }

    @Test
    public void shouldStopParsingOnWrongContentLengthInBeforeBoundary() throws MalformedInputException {
        String data = "----------------------------------------------------------" + boundary + nl +
                "Content-Disposition: form-data; name=\"field_1\"" + nl +
                nl +
                "A123" + nl +
                "--" + boundary + nl + nl;

        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), 5, boundary, temporaryUploadsDirectory);

        try {
            mrh.handle();
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }

        assertThat(mrh.getPost().size(), is(0));
    }


//    @Test
//    public void shouldStopParsingOnWrongContentLengthInBody() throws MalformedInputException {
//        String begin = "--" + boundary + nl +
//                "Content-Disposition: form-data; name=\"field_1\"" + nl +
//                nl +
//                "A123" + nl;
//
//        String data = begin +
//                "--" + boundary + nl +
//                "Content-Disposition: form-data; name=\"field_2\"" + nl +
//                nl +
//                "B123" + nl +
//                "--" + boundary + nl +
//                "Content-Disposition: form-data; name=\"field_3\"" + nl +
//                nl +
//                "C123" + nl +
//                "--" + boundary + nl + nl;
//
//        MultipartRequestHandler mrh = new MultipartRequestHandler(getStreamOutOfString(data), begin.length(), boundary, temporaryUploadsDirectory);
//
//        try {
//            mrh.handle();
//        } catch (IOException e) {
//            fail("Should not throw IOException: " + e.getMessage());
//        }
//
//        assertThat(mrh.getPost().size(), is(3));
//
//        assertThat(mrh.getPost().get("field_1"), is("A123"));
//        assertThat(mrh.getPost().get("field_2"), is(nullValue()));
//        assertThat(mrh.getPost().get("field_3"), is(nullValue()));
//    }

    private InputStream getStreamOutOfString(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
}
