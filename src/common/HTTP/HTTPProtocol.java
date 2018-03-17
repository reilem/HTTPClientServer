package common.HTTP;

public enum HTTPProtocol {
    HTTP_0_9, HTTP_1_0, HTTP_1_1, HTTP_2_0;

    public static HTTPProtocol parseProtocol(String str) {
        if (str.equals("")) return HTTPProtocol.HTTP_0_9;
        return HTTPProtocol.valueOf(str.toUpperCase().replaceAll("[/.]", "_"));
    }

    @Override
    public String toString() {
        switch (this){
            case HTTP_2_0: return "HTTP/2.0";
            case HTTP_1_1: return "HTTP/1.1";
            case HTTP_1_0: return "HTTP/1.0";
            default: return "";
        }
    }
}
