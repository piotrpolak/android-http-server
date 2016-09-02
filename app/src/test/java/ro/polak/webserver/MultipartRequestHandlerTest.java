package ro.polak.webserver;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class MultipartRequestHandlerTest {

    String boundary;
    String nl;
    String temporaryUploadsDirectory = System.getProperty("java.io.tmpdir");

    @Before
    public void setUp() {
        boundary = "------------BOUNDARY";
        nl = "\r\n";
    }

    @Test
    public void testBasicFieldsOnly() {
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
        mrh.handle();

        assertEquals(3, mrh.getPost().size());

        assertEquals("A123", mrh.getPost().get("field_1"));
        assertEquals("B123", mrh.getPost().get("field_2"));
        assertEquals("C123", mrh.getPost().get("field_3"));
    }

    @Test
    public void testBoundaryLikeFieldValues() {
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
        mrh.handle();

        assertEquals(3, mrh.getPost().size());

        assertEquals("--------------BOUNDAR", mrh.getPost().get("field_0"));
        assertEquals("------------BOUNDARY", mrh.getPost().get("field_1"));
        assertEquals("------------BOUNDARY-", mrh.getPost().get("field_2"));
    }

    @Test
    public void testBasicFileUpload() {
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
        mrh.handle();

        assertEquals(1, mrh.getPost().size());

        assertEquals("A123", mrh.getPost().get("field_1"));
        assertEquals(1, mrh.getUploadedFiles().size());
        assertEquals(4, mrh.getUploadedFiles().firstElement().getFile().length());
        // TODO Check file content
    }

    @Test
    public void testFieldsOnBufferMargin() {
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
        mrh.handle();

        assertEquals("A", mrh.getPost().get("field_01"));
        assertEquals("BB", mrh.getPost().get("field_02"));
        assertEquals("CCC", mrh.getPost().get("field_03"));
        assertEquals("DDDD", mrh.getPost().get("field_04"));
        assertEquals("EEEEE", mrh.getPost().get("field_05"));
        assertEquals("FFFFFF", mrh.getPost().get("field_06"));
        assertEquals("GGGGGGG", mrh.getPost().get("field_07"));
        assertEquals("HHHHHHHH", mrh.getPost().get("field_08"));
        assertEquals("IIIIIIIII", mrh.getPost().get("field_09"));
        assertEquals("JJJJJJJJJJ", mrh.getPost().get("field_10"));
        assertEquals("KKKKKKKKKKK", mrh.getPost().get("field_11"));
    }

    private InputStream getStreamOutOfString(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
}
