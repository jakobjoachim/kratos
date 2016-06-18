package Tools;

public class JsonErrorGenerator {
    public static String getErrorJsonString(int status, String message) {
        return String.format("{ \"status\": %s, \"error\": \"%s\" }", status + "", message);
    }
}
