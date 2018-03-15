package com.reinert.common.HTTP;

public enum HTTPStatus {
    CODE_200("OK"),
    CODE_302("Found"),
    CODE_400("Bad Request"),
    CODE_404("Not Found"),
    CODE_500("Internal Server Error");

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
