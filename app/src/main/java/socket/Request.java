package socket;

public class Request {

    // Enumeration for RequestType
    public enum RequestType {
        LOGIN, REGISTER, UPDATE_PAIRING, SEND_INVITATION, ACCEPT_INVITATION, DECLINE_INVITATION, ACKNOWLEDGE_RESPONSE, REQUEST_MOVE, SEND_MOVE, ABORT_GAME, COMPLETE_GAME
    }

    // Class Attributes
    private RequestType type;
    private String data; // Serialized data as a String

    // Default Constructor
    public Request() {
    }

    // Constructor with all attributes
    public Request(RequestType type, String data) {
        this.type = type;
        this.data = data;
    }

    // Getters and Setters for all attributes
    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
