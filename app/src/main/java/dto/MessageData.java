package dto;

public class MessageData {

    private String userId;
    private String otherId;
    private String roomName;
    private String content;
    private String time;

    public MessageData() {}

    public MessageData(String userId, String otherId, String roomName, String content, String time) {
        this.userId = userId;
        this.otherId = otherId;
        this.roomName = roomName;
        this.content = content;
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public MessageData setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getOtherId() {
        return otherId;
    }

    public MessageData setOtherId(String otherId) {
        this.otherId = otherId;
        return this;
    }

    public String getRoomName() {
        return roomName;
    }

    public MessageData setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public MessageData setContent(String message) {
        this.content = message;
        return this;
    }

    public String getTime() {
        return time;
    }

    public MessageData setTime(String time) {
        this.time = time;
        return this;
    }

}
