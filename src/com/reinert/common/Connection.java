package com.reinert.common;

public enum Connection {
    CLOSE, KEEP_ALIVE;

    static Connection parseConnection(String str) {
        if (str.toLowerCase().equals("close")) {
            return Connection.CLOSE;
        } else {
            return Connection.KEEP_ALIVE;
        }
    }

    public boolean keepAlive() {
        return !this.equals(CLOSE);
    }

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
    }
}