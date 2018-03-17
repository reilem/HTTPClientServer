package common.HTTP;

public enum HTTPStatus {
    CODE_200("OK"),
    CODE_301("Moved Permanently"),
    CODE_302("Found"),
    CODE_304("Not Modified"),
    CODE_400("Bad Request"),
    CODE_403("Forbidden"),
    CODE_404("Not Found"),
    CODE_411("Length Required"),
    CODE_500("Internal Server Error"),
    CODE_501("Not Implemented"),
    CODE_505("HTTP Version Not Supported");

    private final String message;

    HTTPStatus(String message) {
        this.message = message;
    }

    public static HTTPStatus getStatusFor(int code) {
        return HTTPStatus.valueOf("CODE_"+code);
    }

    @Override
    public String toString() {
        return this.name().substring(5) + " " + this.message;
    }
}
