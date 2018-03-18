package common.HTTP;

import java.io.*;

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
     * @throws IOException  If something goes wrong during file writing.
     */
    public void writeDataToFile(String filePath) throws IOException {
        FileOutputStream fos = null;
        try {
            // Attempt to open a file output stream at this position
            fos = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            // If file not found, create the needed directories
            File newFile = new File(filePath);
            if (newFile.getParentFile().mkdirs()) fos = new FileOutputStream(filePath);
        }
        // Write the data to the file output stream
        if (fos != null) fos.write(data);
        else throw new FileNotFoundException();
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
