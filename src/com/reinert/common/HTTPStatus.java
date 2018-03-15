package com.reinert.common;

public enum HTTPStatus {
    CODE_200,
    CODE_302,
    CODE_400,
    CODE_404,
    CODE_500;

    public static HTTPStatus getStatusFor(int code) {
        return HTTPStatus.valueOf("CODE_"+code);
    }
}
