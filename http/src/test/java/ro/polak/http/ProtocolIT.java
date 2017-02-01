package ro.polak.http;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * @url https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
 */
public class ProtocolIT extends AbstractIT {

    @Test(expected = IOException.class)
    public void shouldCloseSocketAfterCloseConnectionRequest() throws IOException, InterruptedException {
        String requestBody = RequestBuilder.defaultBuilder()
                .get("/example/Index.dhtml")
                .withHost(HOST + ":" + PORT)
                .withCloseConnection()
                .toString();

        OutputStream out = null;
        try {
            Socket socket = getSocket();
            out = socket.getOutputStream();
            out.write(requestBody.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (in.readLine() != null) {
                // Simulate reading
            }

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

    @Test
    public void shouldServeDirectoryIndex() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/example/"))
                .get()
                .build();

        Request request2 = new Request.Builder()
                .url(getFullUrl("/example/Index.dhtml"))
                .get()
                .build();

        String commonValue = "<h1>Hello World!</h1>";

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString(commonValue));

        Response response2 = client.newCall(request2).execute();
        assertThat(response2.isSuccessful(), is(true));
        assertThat(response2.code(), is(200));
        String response2BodyString = response2.body().string();
        assertThat(response2BodyString, not(isEmptyOrNullString()));
        assertThat(response2BodyString, containsString(commonValue));
    }

    @Test
    public void shouldServeStaticFile() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/staticfile.html"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, is("Static file"));
    }

    @Test
    public void shouldReturn404NotFound() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/43524938257493852435/SOMEUNKNOWNURL.html"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(404));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString("File Not Found"));
    }

    @Test
    public void shouldReturn403ForbiddenOnIllegalPath() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get("../../../index.html")
                .withCloseConnection();

        expectCode(requestBuilder, 403);
    }

    @Test
    public void shouldReturn405MethodNotAllowed() throws IOException {
        // Connect is not yet implemented

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/"))
                .method("CONNECT", null)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(405));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString("Method Not Allowed"));
    }


    @Test
    public void shouldReturn400OnMalformedStatus() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get(null)
                .withCloseConnection();

        expectCode(requestBuilder, 400);
    }

    @Test
    public void shouldReturn414StatusTooLong() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .get(getTooLongUri(2047))
                .withCloseConnection()
                .withProtocol("HTTTTTTTTTTTTTP/4.4");

        expectCode(requestBuilder, 414);
    }

    @Test
    public void shouldReturn414URITooLong() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl(getTooLongUri(2048)))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(414));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString("URI Too Long"));
    }

    @Test
    public void shouldReturn411LengthRequiredForPost() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withCloseConnection();

        expectCode(requestBuilder, 411);
    }

    @Test
    public void shouldReturn400WhenLengthMalformedForPost() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withHeader(Headers.HEADER_CONTENT_LENGTH, "Illegal value")
                .withCloseConnection();

        expectCode(requestBuilder, 400);
    }

    @Test
    public void shouldAcceptPostWithZeroLength() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/example/"))
                .addHeader(Headers.HEADER_CONTENT_LENGTH, "0")
                .post(new FormBody.Builder().build())
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.code(), is(200));
        assertThat(response.body().string(), not(isEmptyOrNullString()));
    }

    @Test
    public void shouldReturn411LengthRequiredForPostMultiPart() throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
                .method("POST", "/example/")
                .withHeader(Headers.HEADER_CONTENT_TYPE, "multipart/mixed; boundary=s9xksnd72SSHu")
                .withCloseConnection();

        expectCode(requestBuilder, 411);
    }

    @Test
    public void shouldReturn400BadRequestOnUnrecognizedMethod() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/"))
                .method("ABC", null)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(400));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString("Bad Request"));
    }

    @Test
    public void shouldReturn400BadRequestOnTooLongMethod() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/"))
                .method("ABCABCABCABCABC", null)
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(400));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString("Bad Request"));
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
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(getFullUrl("/example/InternalServerError.dhtml"))
                .get()
                .build();

        Response response = client.newCall(request).execute();
        assertThat(response.isSuccessful(), is(false));
        assertThat(response.code(), is(500));
        String responseBodyString = response.body().string();
        assertThat(responseBodyString, not(isEmptyOrNullString()));
        assertThat(responseBodyString, containsString("Error 500"));
        assertThat(responseBodyString, containsString("InternalServerError.java"));
    }

//    @Test
//    public void shouldReturn416RangeNotSatisfiable() {
//        // TODO implement
//    }
//
//    @Test
//    public void shouldReturn431RequestHeaderFieldsTooLarge() {
//        // TODO implement
//    }
//
//    @Test
//    public void shouldReturn413PayloadTooLarge() {
//        // maxPostSize 2mb
//        // TODO implement
//    }
//    @Test
//    public void shouldReturn505HTTPVersionNotSupported() {
//        RequestBuilder requestBuilder = RequestBuilder.defaultBuilder()
//                .get("SomeUrl.html")
//                .withHost(HOST + ":" + PORT)
//                .withProtocol("HTTP/9.0")
//                .withCloseConnection();
//
//        expectCode(requestBuilder, 503);
//    }

    private void expectCode(RequestBuilder requestBuilder, int code) throws IOException {
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

    private String getTooLongUri(int length) {
        // 2048 characters seems reasonable
        // see http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url-in-different-browsers
        char[] uri = new char[length + 1];
        uri[0] = '/';
        for (int i = 1; i <= length; i++) {
            uri[i] = 'a';
        }

        return new String(uri);
    }
}
