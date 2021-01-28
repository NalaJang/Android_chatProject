package dto;

public class Message {

    private String signal;
    private String toId;
    private String fromId;
    private String message;
    private String time;
    private String roomId;
    private String photo;

    public Message() {}

    public Message(String signal, String toId, String fromId, String message, String time, String roomId, String photo) {
        this.signal = signal;
        this.toId = toId;
        this.fromId = fromId;
        this.message = message;
        this.time = time;
        this.roomId = roomId;
        this.photo = photo;
    }

    public String getSignal() {
        return signal;
    }

    public Message setSignal(String signal) {
        this.signal = signal;
        return this;
    }

    public String getToId() {
        return toId;
    }

    public Message setToId(String toId) {
        this.toId = toId;
        return this;
    }

    public String getFromId() {
        return fromId;
    }

    public Message setFromId(String fromId) {
        this.fromId = fromId;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Message setTime(String time) {
        this.time = time;
        return this;
    }

    public String getRoomId() {
        return roomId;
    }

    public Message setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public Message setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "signal='" + signal + '\'' +
                ", toId='" + toId + '\'' +
                ", fromId='" + fromId + '\'' +
                ", message='" + message + '\'' +
                ", time='" + time + '\'' +
                ", roomId='" + roomId + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
