package common.HTTP.message;

import com.reinert.common.HTTP.*;
import common.HTTP.HTTPBody;
import common.HTTP.HTTPField;
import common.HTTP.HTTPMethod;
import common.HTTP.HTTPUtil;
import common.HTTP.exceptions.ContentLengthRequiredException;
import common.HTTP.header.HTTPRequestHeader;

import java.io.*;

public class HTTPRequest extends HTTPMessage {

    private HTTPRequestHeader header;

    public HTTPRequest() {}

    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    public void fetchRequest(InputStream inputStream) throws IOException, ContentLengthRequiredException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        this.header = httpInputStream.getRequestHeader();
        HTTPMethod method =  this.header.getMethod();
        Object length = this.header.getFieldValue(HTTPField.CONTENT_LENGTH);
        Object encoding = this.header.getFieldValue(HTTPField.TRANSFER_ENCODING);
        if (method.requiresBody() && length == null && (encoding == null || !encoding.equals(HTTPUtil.CHUNKED))) {
            System.out.println(Thread.currentThread().getName() + ": request received.");
            System.out.println(this.header.toString());
            throw new ContentLengthRequiredException();
        }
        this.fetchBody(httpInputStream);
    }

    public void sendRequest(OutputStream outputStream) throws IOException {
        // Create http output stream
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        // Send message via the stream
        httpOutputStream.sendMessage(this.header, this.body);
        System.out.println("Request sent...");
    }

    @Override
    public HTTPRequestHeader getHeader() { return header; }
}
