package socket;

public class GamingResponse extends Response {
    // Class Attributes
    int move;
    boolean active;

    // Default Constructor
    public GamingResponse() {
        super(); // Call the constructor of the superclass
    }

    // Constructor with all attributes
    public GamingResponse(ResponseStatus status, String message, int move, boolean active) {
        super(status, message); // Call the constructor of the superclass
        this.move = move;
        this.active = active;
    }

    // Getters and Setters for all attributes
    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

