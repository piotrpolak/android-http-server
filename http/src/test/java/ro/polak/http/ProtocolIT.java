package ro.polak.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * The core integration test that verifies protocol compliance.
 * <p>
 * See https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 * See https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers
 */
// CHECKSTYLE.OFF: MagicNumber
public class ProtocolIT extends AbstractIT {

    private static final String NEW_LINE = "\r\n";
    public static final String DASH_DASH = "--";
    private static OkHttpClient client;
    private static final int TIMEOUT_IN_SECONDS = 600;

    @Before
    public void init() {
        client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .cookieJar(new MyCookieJar())
                .build();
    }

    @Test(expected = IOException.class)
    public void shouldCloseSocketAfterCloseConnectionRequest() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/example/Index")
                .withHost(HOST + ":" + PORT)
                .withCloseConnection()
                .toString();

        OutputStream out = null;
        try {
            Socket socket = getSocket();
            out = socket.getOutputStream();
            out.write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            simulateReading(in);

        } catch (IOException e) {
            fail("The test failed too early due IOException" + e.getMessage());
        }

        int i = 0;
        while (i++ < 10) {
            // The following code will cause error on a closed socket
            Thread.sleep(100);
            out.write("X".getBytes());
            out.flush();
        }
    }

    // CHECKSTYLE.OFF: EmptyBlock
    private void simulateReading(final BufferedReader in) throws IOException {
        while (in.readLine() != null) {
            // Simulate reading
        }
    }
    // CHECKSTYLE.ON: EmptyBlock

    @Test
    public void shouldServeDirectoryForServletIndex() throws IOException {
        assertThat(shouldServeDirectoryFile("/example/", "/example/Index", "<h1>Hello World!</h1>"), is(true));
    }

    @Test
    public void shouldServeDirectoryForFileIndex() throws IOException {
        assertThat(shouldServeDirectoryFile("/", "/index.html", "Index file"), is(true));
    }

    @Test
    public void shouldRedirectToDirectoryIndexOnMissingTrailingSlash() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/static"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.code(), is(301));
        assertThat(response.header("Location"), is(not(nullValue())));
        assertThat(response.header("Location"), is("/static/"));
    }

    private boolean shouldServeDirectoryFile(final String pathShort, final String pathFull, final String commonValue)
            throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl(pathShort))
                .get()
                .build();

        Request request2 = new Request.Builder()
                .url(getFullUrl(pathFull))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString(commonValue));

        Response response2 = client.newCall(request2).execute();
        assertThat(response2.isSuccessful(), is(true));
        assertThat(response2.code(), is(200));
        String response2BodyString = response2.body().string();
        assertThat(response2BodyString, is(not(emptyOrNullString())));
        assertThat(response2BodyString, containsString(commonValue));

        return true;
    }

    @Test
    public void shouldServeStaticFile() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/staticfile.html"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        assertThat(response.header(Headers.HEADER_CONTENT_LENGTH), is("11"));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, is("Static file"));
    }

    @Test
    public void shouldOpenAndCloseSession() throws IOException {
        assertThat(shouldOpenAndCloseSession(1), is(true));
        assertThat(shouldOpenAndCloseSession(2), is(true));
    }

    private boolean shouldOpenAndCloseSession(final int count) throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/example/Session"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
//        assertThat(response.header(Headers.HEADER_CONTENT_LENGTH), is(not(nullValue())));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Session page hits: " + count));

        return true;
    }

    @Test
    public void shouldReturn404NotFound() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/43524938257493852435/SOMEUNKNOWNURL.html"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(404));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("File Not Found"));
    }

    @Test
    public void shouldReturn403ForbiddenOnIllegalPath() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get("../../../index.html")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 403);
    }

    @Test
    public void shouldReturn403ForbiddenOnBlockedByFilter() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get("/example/secured/ForbiddenByFilter")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 403);
    }

    @Test
    public void shouldReturn200AndRespectFilterExclude() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get("/example/secured/Logout")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 404);
    }

    @Test
    public void shouldReturn403ForbiddenOnBlockedByFilterOnNonExistentUrl() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get("/example/secured/thisUrlDoesNotExist")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 403);
    }

    @Test
    public void shouldReturn405MethodNotAllowed() throws IOException {
        // Connect is not yet implemented
        Request request = new Request.Builder()
                .url(getFullUrl("/"))
                .method("CONNECT", null)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(405));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Method Not Allowed"));
    }


    @Test
    public void shouldReturn400OnMalformedStatus() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get(null)
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 400);
    }

    @Test
    public void shouldReturn414StatusTooLong() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get(getTooLongUri(2047))
                .withCloseConnection()
                .withProtocol("HTTTTTTTTTTTTTP/4.4");

        assertResponsesWithHttpCode(requestBuilder, 414);
    }

    @Test
    public void shouldReturn414URITooLong() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl(getTooLongUri(2048)))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(414));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("URI Too Long"));
    }

    @Test
    public void shouldReturn411LengthRequiredForPost() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 411);
    }

    @Test
    public void shouldReturn400WhenLengthMalformedForPost() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withHeader(Headers.HEADER_CONTENT_LENGTH, "Illegal value")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 400);
    }

    @Test
    public void shouldAcceptPostWithZeroLength() throws IOException {

        Request request = new Request.Builder()
                .url(getFullUrl("/example/"))
                .addHeader(Headers.HEADER_CONTENT_LENGTH, "0")
                .post(new FormBody.Builder().build())
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), is(not(emptyOrNullString())));
    }

    @Test
    public void shouldReturn411LengthRequiredForPostMultiPart() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withHeader(Headers.HEADER_CONTENT_TYPE, "multipart/mixed; boundary=s9xksnd72SSHu")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 411);
    }

    @Test
    public void shouldReturn400BadRequestOnUnrecognizedMethod() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/"))
                .method("ABC", null)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(400));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Bad Request"));
    }

    @Test
    public void shouldReturn400BadRequestOnTooLongMethod() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/"))
                .method("ABCABCABCABCABC", null)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(400));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Bad Request"));
    }

    @Test
    public void shouldReturn200ForPlainPost() throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("someParam", "someValue")
                .build();

        Request request = new Request.Builder()
                .url(getFullUrl("/example/"))
                .method("POST", RequestBody.create(new byte[0]))
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
    }

    @Test
    public void shouldReturn200ForMultipartFormPost() throws IOException {
        // Based on https://stackoverflow.com/questions/24279563/uploading-a-large-file-in-multipart-using-okhttp

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("someParam", "someValue")
                .build();

        Request request = new Request.Builder()
                .url(getFullUrl("/example/"))
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
    }

    @Test
    public void shouldReturn200ForMultipartFilePost() throws IOException {
        File uploadFile = createRandomContentsFile();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("someParam", "someValue")
                .addFormDataPart("file", "somefile.dat",
                        RequestBody.create(uploadFile, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(getFullUrl("/example/"))
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
    }

    @Test
    public void shouldReturn200ChunkedResponse() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/example/Chunked"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        // CHECKSTYLE.OFF: LineLength
        assertThat(responseBodyString, is("This is an example of chunked transfer type. Chunked transfer type can be used when the final length of the data is not known."));
        // CHECKSTYLE.ON: LineLength
    }

    private File createRandomContentsFile() throws IOException {
        File file = File.createTempFile("servertest", ".tmp");
        RandomAccessFile f = new RandomAccessFile(file, "rw");
        f.setLength(1024);
        return file;
    }

    @Test
    public void shouldHangSilentlyOnClosingSocket() throws IOException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/43524938257493852435/SOMEUNKNOWNURL.html")
                .withCloseConnection()
                .toString();

        Socket socket = getSocket();
        OutputStream out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 404"));
                socket.close();
                break;
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }
    }

    @Test
    public void shouldReturn500InternalServerError() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/example/InternalServerError"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(500));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Error 500"));
        assertThat(responseBodyString, containsString("UnexpectedSituationException"));
    }

    @Test
    public void shouldReturn206AndServeRangesOfStaticFileForOneRange() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/staticfile.html"))
                .header("Range", "bytes=0-5")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(206));
        assertThat(response.header(Headers.HEADER_CONTENT_RANGE), is("bytes 0-5/11"));
        assertThat(response.header(Headers.HEADER_CONTENT_LENGTH), is("6"));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, is("Static"));
    }

    @Test
    public void shouldReturn206AndServeRangesOfStaticFileForMultipleRanges() throws IOException {
        String fileLength = "11";
        String ranges = "0-5,7-10";
        String boundaryBegin = "multipart/byteranges; boundary=";

        Request request = new Request.Builder()
                .url(getFullUrl("/staticfile.html"))
                .header(Headers.HEADER_RANGE, "bytes=" + ranges)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(206));

        assertThat(response.header(Headers.HEADER_CONTENT_TYPE), startsWith(boundaryBegin));
        assertThat(response.header(Headers.HEADER_CONTENT_RANGE),
                is("bytes " + ranges + "/" + fileLength));

        String boundary = response.header(Headers.HEADER_CONTENT_TYPE)
                .substring(boundaryBegin.length());

        String expectedResponseStr = NEW_LINE
                + DASH_DASH
                + boundary
                + NEW_LINE
                + "Content-Type: text/html"
                + NEW_LINE
                + "Content-Range: bytes 0-5/" + fileLength
                + NEW_LINE
                + NEW_LINE
                + "Static"
                + NEW_LINE
                + DASH_DASH
                + boundary
                + NEW_LINE
                + "Content-Type: text/html"
                + NEW_LINE
                + "Content-Range: bytes 7-10/" + fileLength
                + NEW_LINE
                + NEW_LINE
                + "file"
                + NEW_LINE
                + DASH_DASH
                + boundary
                + DASH_DASH
                + NEW_LINE;

        assertThat(Integer.valueOf(response.header(Headers.HEADER_CONTENT_LENGTH)),
                is(expectedResponseStr.length()));

        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, is(expectedResponseStr));

    }

    @Test
    public void shouldReturn416RangeNotSatisfiable() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/staticfile.html"))
                .header(Headers.HEADER_RANGE, "bytes=128-128")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(416));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Range Not Satisfiable"));
    }

    @Test
    public void shouldReturn400OnMalformedRange() throws IOException {
        Request request = new Request.Builder()
                .url(getFullUrl("/staticfile.html"))
                .header(Headers.HEADER_RANGE, "bytes=128-abcd")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(400));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, is(not(emptyOrNullString())));
        assertThat(responseBodyString, containsString("Bad Request"));
    }

    //
//    @Test
//    public void shouldReturn431RequestHeaderFieldsTooLarge() {
//        // TODO implement
//    }
//
    @Test
    public void shouldReturn413PayloadTooLarge() throws IOException {
        int length = 50 * 1024 * 1024 + 1;

        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withHeader(Headers.HEADER_CONTENT_LENGTH, Integer.toString(length))
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 413);
    }

    @Test
    public void shouldReturn505HTTPVersionNotSupported() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get("SomeUrl.html")
                .withHost(HOST + ":" + PORT)
                .withProtocol("HTTP/9.0")
                .withCloseConnection();

        assertResponsesWithHttpCode(requestBuilder, 505);
    }

    private void assertResponsesWithHttpCode(final RequestBuilder requestBuilder, final int code) throws IOException {
        String requestBody = requestBuilder.toString();

        Socket socket = getSocket();
        OutputStream out = socket.getOutputStream();
        out.write(requestBody.getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line;
        int numberOfLinesRead = 0;
        while ((line = in.readLine()) != null) {
            if (++numberOfLinesRead == 1) {
                assertThat(line, startsWith("HTTP/1.1 " + code));
                break;
            }
        }

        if (numberOfLinesRead == 0) {
            fail("No server response was read");
        }

        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    private String getTooLongUri(final int length) {
        // 2048 characters seems reasonable
        // see http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url-in-different-browsers
        char[] uri = new char[length + 1];
        uri[0] = '/';
        for (int i = 1; i <= length; i++) {
            uri[i] = 'a';
        }

        return new String(uri);
    }

    /**
     * All credits go to gncabrera.
     *
     * @see <a href="https://stackoverflow.com/a/34884863/2298527">https://stackoverflow.com/a/34884863/2298527</a>
     */
    public final class MyCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(final HttpUrl url, final List<Cookie> cookies) {
            this.cookies = cookies;
        }

        @Override
        public List<Cookie> loadForRequest(final HttpUrl url) {
            if (cookies != null) {
                return cookies;
            }
            return new ArrayList<>();

        }
    }
}
// CHECKSTYLE.ON: MagicNumber
