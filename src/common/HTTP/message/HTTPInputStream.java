package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPField;
import common.HTTP.HTTPUtil;
import common.HTTP.header.HTTPHeader;
import javafx.util.Pair;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Class for HTTPInputStreams. Provides methods for reading HTTP headers and HTTP message bodies.
 */
public class HTTPInputStream {

    // Current InputStream from which all data will be read
    private final InputStream inputStream;

    /**
     * Constructor for a HTTPInputStream.
     * @param inputStream The input stream from which all data will be read
     */
    HTTPInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Read the body on the current input stream with chunked transfer encoding.
     * @return              Body data read from the input stream. In HTTPBody format.
     * @throws IOException  IF something goes wrong during reading.
     */
    public HTTPBody getChunkedBody() throws IOException {
        // Create a ByteArrayOutputStream to stores bytes
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        // Next byte to be read
        int nextByte;
        // Last four bytes of the stream
        byte[] endBytes = {13, 10, 13, 10};
        // While nextByte is valid
        while ((nextByte = inputStream.read()) != -1) {
            // Write nextByte to byte array
            byteArray.write(nextByte);
            // Get size of current byteArray
            int l = byteArray.size();
            // If last four bytes equal the end bytes, break
            if (l >= 5 && Arrays.equals(Arrays.copyOfRange(byteArray.toByteArray(), l-4, l), endBytes)) break;
        }
        // Return byteArray data as a HTTPBody
        return new HTTPBody(byteArray.toByteArray());
    }

    /**
     * Read the body on the current input stream based on given buffer size.
     * @param bufferSize    Length of expected content and size of buffer.
     * @return              Body data read from input stream. In HTTPBody format.
     * @throws IOException  If something goes wrong during reading.
     */
    public HTTPBody getBufferedBody(int bufferSize) throws IOException {
        // Make buffer byte array
        byte[] buffer = new byte[bufferSize];
        // Create a buffered input stream based on given input stream.
        BufferedInputStream in = new BufferedInputStream(inputStream);
        // Store amount of read data in dataSize variable
        int dataSize = 0;
        while (dataSize < bufferSize) {
            // Read the next bytes in to the buffer, using dataSize as offset and the diff between bufferSize and
            // dataSize to indicate amount of space left in the buffer.
            int nextDataLen = in.read(buffer, dataSize, bufferSize - dataSize);
            // If next data length is invalid break
            if (nextDataLen <= -1) break;
            // Increase dataSize with next data length
            dataSize += nextDataLen;
        }
        // Return read data in buffer as a HTTPBody
        return new HTTPBody(buffer);
    }

    /**
     * Fills in the given header with any remaining header fields left in the input stream.
     * @param header        The given header that will be filled in.
     * @throws IOException  If something goes wrong during reading.
     */
    public void fillHeaderFields(HTTPHeader header) throws IOException {
        // Init a nextEntry variable.
        Pair<HTTPField, Object> nextEntry;
        // While next header line is not null
        while ((nextEntry = getNextHeaderLine()) != null) {
            // Add the field and value in the entry to the header.
            header.addField(nextEntry.getKey(), nextEntry.getValue());
        }
    }

    /**
     * Get the next line on the input stream.
     * @return              Next line on the input stream as a string.
     * @throws IOException  If something goes wrong during reading.
     */
    String getNextLine() throws IOException {
        // Store the next line in a string builder
        StringBuilder nextLine = new StringBuilder();
        // While nextLine does not end with CRLF
        while (!nextLine.toString().endsWith(HTTPUtil.CRLF)) {
            // Append the next character on the input stream
            nextLine.append((char)this.inputStream.read());
        }
        // Return the nextLine as a string without CRLF.
        return stripCRLF(nextLine.toString());
    }

    /**
     * Gets the next header line on the input stream
     * @return              Next headerLine as a Pair of HTTPField and Object.
     * @throws IOException  If something goes wrong during reading.
     */
    private Pair<HTTPField, Object> getNextHeaderLine() throws IOException {
        // Get the next line from the input stream as string.
        String nextLine = this.getNextLine();
        // If the next line is equal to CRLF return null.
        if (nextLine.equals(HTTPUtil.CRLF)) return null;
        // Split the next line on a colon-space.
        String[] split = nextLine.split(": ");
        // Return null if split is invalid.
        if (split.length < 2) return null;
        // Get the header field and value strings.
        String headerField = split[0].trim();
        String headerValue = split[1].trim();
        try {
            // Attempt to parse the headerField and value as a HTTPField
            HTTPField f = HTTPField.getFieldFor(headerField);
            // Return a pair with given field and parsed field value.
            return new Pair<>(f, f.parseValueString(headerValue));
        } catch (IllegalArgumentException e) {
            // If parsing fails, return a pair with field type OTHER and the entire nextLine as value.
            return new Pair<>(HTTPField.OTHER, nextLine);
        }
    }

    /**
     * Strips the "\r\n" characters from given string.
     */
    private String stripCRLF(String s) {
        return s.replaceAll("\r\n", "");
    }

}
