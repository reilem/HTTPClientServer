package common.HTTP.header;

import common.HTTP.Connection;
import common.HTTP.HTTPField;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An abstract class for HTTP Headers. Contains a HTTPProtocol, a HashMap containing implemented header fields and an
 * ArrayList of strings for any unimplemented header fields.
 */
public abstract class HTTPHeader {

    // Protocol stored in the current header
    protected final HTTPProtocol protocol;

    // HashMap mapping HTTPField enums to their given value in the current header
    private HashMap<HTTPField, Object> fields = new HashMap<>();
    // ArrayList of Strings for all given header fields which do not have an equivalent HTTPField implementation
    private ArrayList<String> other = new ArrayList<>();

    /**
     * Constructor for a HTTPHeader.
     */
    HTTPHeader(HTTPProtocol protocol) {
        this.protocol = protocol;
    }

    /**
     * Abstract method which will get the main line of the header. This will either be the status-line in case of a
     * response header or the request-line in case of a request.
     * @return Main line of the header as a string.
     */
    abstract String getHeaderMainLine();

    /**
     * Adds a new field to either the fields HashMap or other ArrayList.
     * @param field HTTPField key for the fields HashMap.
     * @param value Object value for the fields HashMap.
     */
    public void addField(HTTPField field, Object value) {
        // If given field is null do nothing.
        if (field == null) return;
        if (!field.equals(HTTPField.OTHER) && value != null && field.isValidValueType(value)) {
            // If field is not of type OTHER and the value is not null and valid, the put this key value
            // pair in the fields HashMap.
            fields.put(field, value);
        } else if (!field.equals(HTTPField.OTHER) && value == null) {
            // If field is not of type OTHER but the value is null, remove the field from the HashMap.
            fields.remove(field);
        } else {
            // If the field is of type OTHER, then add the value to the other ArrayList.
            other.add((String)value);
        }
    }

    /**
     * Gets the value of given field.
     * @param field HTTPField to be checked.
     * @return The value of the given field in the fields HashMap or null.
     */
    public Object getFieldValue(HTTPField field) {
        // If field is not contained in the fields HashMap, return null.
        if (!fields.containsKey(field)) return null;
        // Else return the value found in the fields HashMap.
        return fields.get(field);
    }

    /**
     * Gets the protocol stored in the current header.
     */
    public HTTPProtocol getProtocol() {
        return protocol;
    }

    /**
     * Checks if the connection should be kept alive based on the information stored in current header.
     * @return True if the connection should be kept alive. False otherwise.
     */
    public boolean keepConnectionAlive() {
        // Get the connection value stored in current header.
        Connection connection = (Connection)this.getFieldValue(HTTPField.CONNECTION);
        // Return true if protocol is http 1.1, connection is null or equals keep-alive. Or if protocol is http 1.0,
        // connection is not null and equals keep-alive.
        return (protocol.equals(HTTPProtocol.HTTP_1_1) && (connection == null || connection.equals(Connection.KEEP_ALIVE))) ||
                (protocol.equals(HTTPProtocol.HTTP_1_0) && connection != null && connection.equals(Connection.KEEP_ALIVE));
    }

    /**
     * Converts the current header into readable and valid HTTP String format using CRLF for new lines.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.getHeaderMainLine());
        s.append(HTTPUtil.CRLF);
        this.fields.forEach((HTTPField field, Object value) -> {
            s.append(field.toString());
            s.append(value != null ? value.toString() : "");
            s.append(HTTPUtil.CRLF);
        });
        this.other.forEach((String str) -> {
            s.append(str);
            s.append(HTTPUtil.CRLF);
        });
        return s.toString();
    }
}
