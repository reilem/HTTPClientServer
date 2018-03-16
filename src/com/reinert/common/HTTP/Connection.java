package com.reinert.common.HTTP;

public enum Connection {
    CLOSE, KEEP_ALIVE;

    static Connection parseConnection(String str) {
        if (str.toLowerCase().equals("close")) {
            return Connection.CLOSE;
        } else {
            return Connection.KEEP_ALIVE;
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
    }
}