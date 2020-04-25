import java.time.ZonedDateTime;

/**
 * Class represing a single entry of the Common Log Format (CLF) :
 * https://en.wikipedia.org/wiki/Common_Log_Format
 * 
 * Ex: 127.0.0.1 user-identifier frank [10/Oct/2000:13:55:36 -0700] "GET
 * /apache_pb.gif HTTP/1.0" 200 2326 A "-" in a field indicates missing data.
 * 
 * 127.0.0.1 is the IP address of the client (remote host) which made the
 * request to the server. user-identifier is the RFC 1413 identity of the
 * client. Usually "-". frank is the userid of the person requesting the
 * document. Usually "-" unless .htaccess has requested authentication.
 * [10/Oct/2000:13:55:36 -0700] is the date, time, and time zone that the
 * request was received, by default in strftime format %d/%b/%Y:%H:%M:%S %z.
 * "GET /apache_pb.gif HTTP/1.0" is the request line from the client. The method
 * GET, /apache_pb.gif the resource requested, and HTTP/1.0 the HTTP protocol.
 * 200 is the HTTP status code returned to the client. 2xx is a successful
 * response, 3xx a redirection, 4xx a client error, and 5xx a server error. 2326
 * is the size of the object returned to the client, measured in bytes.
 */
public class CLFLogEntry {
    // IP address of the client (remote host) which made the request to the server.
    private final String remoteHost;

    // the RFC 1413 identity of the client. Usually "-".
    private final String userIdentifier;

    // the userid of the person requesting the document. Usually "-" unless
    // .htaccess has requested authentication.
    private final String userId;

    // the date, time, and time zone that the request was received, by default in
    // strftime format %d/%b/%Y:%H:%M:%S %z (parsed from this format).
    private final ZonedDateTime timestamp;

    /**
     * "GET /apache_pb.gif HTTP/1.0" is the request line from the client. The method
     * GET, /apache_pb.gif the resource requested, and HTTP/1.0 the HTTP protocol.
     */
    // the HTTP method (GET in our above example).
    private final String httpMethod;

    // the resource requested through the HTTP method (/apache_pb.gif in our above
    // example).
    private final String resource;

    // the HTTP protocol (/apache_pb.gif in our above example).
    private final String httpProtocol;

    // the HTTP status code returned to the client. 2xx is a successful response,
    // 3xx a redirection, 4xx a client error, and 5xx a server error.
    private final int httpStatusCode;

    // the size of the object returned to the client, measured in bytes.
    private final int responseSize;

    public String getRemoteHost() {
        return remoteHost;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public String getUserId() {
        return userId;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getResource() {
        return resource;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public CLFLogEntry(String remoteHost, String userIdentifier, String userId, ZonedDateTime timestamp,
            String httpMethod, String resource, String httpProtocol, int httpStatusCode, int responseSize) {
        this.remoteHost = remoteHost;
        this.userIdentifier = userIdentifier;
        this.userId = userId;
        this.timestamp = timestamp;
        this.httpMethod = httpMethod;
        this.resource = resource;
        this.httpProtocol = httpProtocol;
        this.httpStatusCode = httpStatusCode;
        this.responseSize = responseSize;
    }
}