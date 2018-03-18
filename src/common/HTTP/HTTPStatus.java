package common.HTTP;

/**
 * Types of HTTP Statuses that can be returned in a HTTP response.
 */
public enum HTTPStatus {
    // Implemented status codes
    CODE_200("OK"),
    CODE_302("Found"),
    CODE_304("Not Modified"),
    CODE_400("Bad Request"),
    CODE_403("Forbidden"),
    CODE_404("Not Found"),
    CODE_411("Length Required"),
    CODE_418("I'm a teapot"),
    CODE_500("Internal Server Error"),
    CODE_501("Not Implemented"),
    CODE_505("HTTP Version Not Supported"),
    // Common unimplemented status codes, made available to prevent parsing errors in case client receives them.
    CODE_100("Continue"),
    CODE_101("Switching Protocols"),
    CODE_102("Processing"),
    CODE_103("Early Hints"),
    CODE_201("Created"),
    CODE_202("Accepted"),
    CODE_203("Non-Authoritative Information"),
    CODE_204("No Content"),
    CODE_205("Reset Content"),
    CODE_206("Partial Content"),
    CODE_300("Multiple Choices"),
    CODE_301("Moved Permanently"),
    CODE_303("See Other"),
    CODE_305("Use Proxy"),
    CODE_307("Temporary Redirect"),
    CODE_401("Unauthorized"),
    CODE_402("Payment Required"),
    CODE_405("Method Not Allowed"),
    CODE_406("Not Acceptable"),
    CODE_407("Proxy Authentication Required"),
    CODE_408("Request Timeout"),
    CODE_409("Conflict"),
    CODE_410("Gone"),
    CODE_412("Precondition Failed"),
    CODE_413("Request Entity Too Large"),
    CODE_414("Request-URI Too Long"),
    CODE_415("Unsupported Media Type"),
    CODE_416("Requested Range Not Satisfiable"),
    CODE_417("Expectation Failed"),
    CODE_502("Bad Gateway"),
    CODE_503("Service Unavailable"),
    CODE_504("Gateway Timeout"),
    CODE_511("Network Authentication Required");

    // Status message, used during toString
    private final String message;

    /**
     * Constructor for HTTPStatus
     * @param message   Standard message associated with status code.
     */
    HTTPStatus(String message) {
        this.message = message;
    }

    /**
     * Parse given integer code value into a valid HTTP Status
     * @param code  Given integer code value.
     * @return      Valid HTTPStatus associated with given code.
     */
    public static HTTPStatus parseStatus(int code) {
        return HTTPStatus.valueOf("CODE_"+code);
    }

    @Override
    public String toString() {
        return this.name().substring(5) + " " + this.message;
    }
}
