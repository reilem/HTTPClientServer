package com.reinert.common.HTTP;

import com.reinert.common.HTTP.exceptions.MethodNotImplementedException;

public enum HTTPMethod {
    GET, PUT, POST, HEAD;

    public static HTTPMethod parseMethod(String str) throws MethodNotImplementedException {
        try {
            return HTTPMethod.valueOf(str.trim().toUpperCase());
        } catch (Exception e) {
            throw new MethodNotImplementedException();
        }

    }

    public boolean requiresBody() {
        return this.equals(PUT) || this.equals(POST);
    }

    public boolean requiresFileData() { return this.equals(GET) || this.equals(HEAD); }
}
