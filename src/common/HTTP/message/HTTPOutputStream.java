package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPUtil;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Class for HTTPOutputStreams. Provides methods for sending HTTP messages.
 */
public class HTTPOutputStream {

    // The current output stream on which a message can be written
    private final OutputStream outputStream;

    /**
     * Constructor for a HTTPOutputStream.
     * @param outputStream The output stream through which messages will be transmitted.
     */
    public HTTPOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Sends a message across the current output stream.
     * @param httpMessage   The message to be sent.
     * @throws IOException  If anything goes wrong during transmission.
     */
    public void sendMessage(HTTPMessage httpMessage) throws IOException {
        // Write the header from the given http Message
        this.outputStream.write(httpMessage.getHeader().toString().getBytes());
        // Get the body from the message
        HTTPBody body = httpMessage.getBody();
        if (body != null) {
            // If the body is not null, write it with surrounding CRLF's
            this.outputStream.write(HTTPUtil.CRLF.getBytes());
            this.outputStream.write(body.getData());
            this.outputStream.write(HTTPUtil.CRLF.getBytes());
        }
        // Write finishing CRLF line
        this.outputStream.write(HTTPUtil.CRLF.getBytes());
        // Flush the output
        this.outputStream.flush();
    }
}
