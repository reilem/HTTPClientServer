package common.HTTP;

import common.HTTP.exceptions.NoContentFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Class representing the body of a HTTP message. Contains a byte array for data storage and methods that can print
 * the data or write the data to a file.s
 */
public class HTTPBody {

    // Data of the http body
    private final byte[] data;

    /**
     * Constructor for a HTTPBody that takes a string as data parameter.
     * @param text  String which will be stored.
     */
    public HTTPBody(String text) {
        this.data = text.getBytes();
    }

    /**
     * Constructor for a HTTPBody that takes a byte array as data parameter.
      * @param data Byte array that will be stored.
     */
    public HTTPBody(byte[] data) {
        this.data = data;
    }

    /**
     * Write the data stored in the current http body to file at given file path.
     * @param filePath      Given file path.
     * @param append        Determines if data should be appended to file or overwritten.
     * @throws IOException  If something goes wrong during file writing.
     */
    public void writeDataToFile(String filePath, boolean append) throws NoContentFoundException {
        if (append) {
            try {
                Files.write(Paths.get(filePath), this.data, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new NoContentFoundException();
            }
        }
        else {
            try {
                Files.write(Paths.get(filePath), this.data);
            } catch (IOException e) {
                // If file not found, create the needed directories
                File newFile = new File(filePath);
                if (newFile.getParentFile().mkdirs()) {
                    try {Files.write(Paths.get(filePath), this.data); } catch (IOException ignored) {}
                }
            }
        }

    }

    /**
     * Get the data from the current body as a a string in given character set.
     * @param charSet                       Given character set.
     * @return                              Current http body data as a string.
     * @throws UnsupportedEncodingException If something goes wrong during string conversion.
     */
    public String getAsString(String charSet) throws UnsupportedEncodingException {
        if (charSet == null) charSet = "UTF-8";
        return new String(data, charSet);
    }

    /**
     * Prints the data to System.out output stream as string in given character set.
     * @param charSet                       Given character set.
     * @throws UnsupportedEncodingException If something goes wrong during string conversion.
     */
    public void printData(String charSet) throws UnsupportedEncodingException {
        System.out.println(this.getAsString(charSet));
    }

    /**
     * Get the byte array data from current HTTP body.
     */
    public byte[] getData() {
        return data;
    }
}
