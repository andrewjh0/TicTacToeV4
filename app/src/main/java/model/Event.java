package model;

public class Event {
    // Enumeration for EventStatus
    public enum EventStatus {
        PENDING, DECLINED, ACCEPTED, PLAYING, COMPLETED, ABORTED
    }

    // Class Attributes
    private int eventId;
    private String sender;
    private String opponent;
    private EventStatus status;
    private String turn;
    private int move;

    // Default Constructor
    public Event() {
    }

    // Constructor with all attributes
    public Event(int eventId, String sender, String opponent, EventStatus status, String turn, int move) {
        this.eventId = eventId;
        this.sender = sender;
        this.opponent = opponent;
        this.status = status;
        this.turn = turn;
        this.move = move;
    }

    // Getters and Setters for all attributes
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }


    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    // Equals method overridden to compare events based on their event IDs
    @Override
    public boolean equals(Object obj) {
        try {
            Event other = (Event) obj;
            return this.eventId == other.getEventId();
        } catch (ClassCastException e) {
            return false;
        }
    }
}
