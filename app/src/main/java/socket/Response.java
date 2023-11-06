package socket;

public class Response {
    public static Object Status;

    // Enumeration for ResponseStatus
    public enum ResponseStatus {
        SUCCESS, FAILURE
    }

    // Class Attributes
    private ResponseStatus status;
    private String message;

    // Default Constructor
    public Response() {
    }

    // Constructor with all attributes
    public Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and Setters for all attributes
    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
