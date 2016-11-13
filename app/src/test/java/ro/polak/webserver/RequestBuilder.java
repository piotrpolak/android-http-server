package ro.polak.webserver;

public class RequestBuilder {

    private final String NEW_LINE = "\r\n";
    private String method;
    private String uri;
    private Headers headers = new Headers();
    private HeadersSerializer headersSerializer = new HeadersSerializer();

    public static RequestBuilder defaultBuilder() {
        RequestBuilder rb = new RequestBuilder();
        return rb;
    }

    public RequestBuilder get(String uri) {
        this.uri = uri;
        method = "GET";
        return this;
    }

    public RequestBuilder method(String method, String uri) {
        this.method = method;
        this.uri = uri;
        return this;
    }

    public RequestBuilder withHeader(String name, String value) {
        headers.setHeader(name, value);
        return this;
    }

    public RequestBuilder withCloseConnection() {
        this.withHeader("Connection", "close");
        return this;
    }

    public RequestBuilder withHost(String value) {
        this.withHeader("Host", value);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        return sb.append(method)
                .append(" ")
                .append(uri)
                .append(" ")
                .append("HTTP/1.1")
                .append(NEW_LINE)
                .append(headersSerializer.serialize(headers))
                .toString();
    }
}
