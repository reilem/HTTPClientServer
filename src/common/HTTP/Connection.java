package common.HTTP;

/**
 * Types of possible connection values passed via HTTP in the "Connection:" HTTP header field.
 */
public enum Connection {
    CLOSE, KEEP_ALIVE;

    /**
     * Parse the given string into a Connection.
     * @param str   String to be parsed.
     * @return      Connection equivalent of given string: "close" returns CLOSE, others return KEEP_ALIVE.
     */
    static Connection parseConnection(String str) {
        if (str.toLowerCase().equals("close")) {
            return Connection.CLOSE;
        } else {
            return Connection.KEEP_ALIVE;
        }
    }

    /**
     * Parse given boolean into a connection.
     * @param alive Boolean to be parsed.
     * @return      Connection equivalent of given bool: true returns KEEP_ALIVE, false returns CLOSE
     */
    public static Connection parseConnection(boolean alive) {
        if (alive) return Connection.KEEP_ALIVE;
        else return Connection.CLOSE;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
    }
}