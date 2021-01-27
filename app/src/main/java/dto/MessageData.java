package dto;

public class MessageData {

    private int num;
    private int unread;  //읽었으면 '0'
    private String userId;
    private String otherId;
    private String roomName;
    private String content;
    private String time;

    @Override
    public String toString() {
        return "MessageData{" +
                "num=" + num +
                ", unread=" + unread +
                ", userId='" + userId + '\'' +
                ", otherId='" + otherId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public MessageData() {}

    public MessageData(int num, int unread, String userId, String otherId, String roomName, String content, String time) {
        this.num = num;
        this.unread = unread;
        this.userId = userId;
        this.otherId = otherId;
        this.roomName = roomName;
        this.content = content;
        this.time = time;
    }

    public int getNum() {
        return num;
    }

    public MessageData setNum(int num) {
        this.num = num;
        return this;
    }

    public int getUnread() {
        return unread;
    }

    public MessageData setUnread(int unread) {
        this.unread = unread;
        return this;
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
