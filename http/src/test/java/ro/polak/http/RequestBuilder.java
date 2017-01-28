package ro.polak.http;

import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.protocol.serializer.impl.HeadersSerializer;

public class RequestBuilder {

    private final String NEW_LINE = "\r\n";
    private String method;
    private String uri;
    private String protocol = "HTTP/1.1";
    private Headers headers = new Headers();
    private Serializer<Headers> headersSerializer = new HeadersSerializer();

    public static RequestBuilder defaultBuilder() {
        RequestBuilder rb = new RequestBuilder();
        return rb;
    }

    public RequestBuilder get(String uri) {
        return method("GET", uri);
    }

    public RequestBuilder withProtocol(String protocol) {
        this.protocol = protocol;
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

        if (method != null) {
            sb.append(method)
                    .append(" ");
        }
        if (uri != null) {
            sb.append(uri)
                    .append(" ");
        }
        if (protocol != null) {
            sb.append(protocol);
        }
        sb.append(NEW_LINE)
                .append(headersSerializer.serialize(headers));


        return sb.toString();
    }
}
