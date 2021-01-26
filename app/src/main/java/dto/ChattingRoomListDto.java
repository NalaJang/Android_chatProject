package dto;

public class ChattingRoomListDto {

    private String roomName;
    private String myId;
    private String otherId;
    private String lastContent;
    private String profileImage;
    private String time;

    public ChattingRoomListDto() {}

    public ChattingRoomListDto(String roomName, String myId, String otherId, String lastContent, String profileImage, String time) {
        this.roomName = roomName;
        this.myId = myId;
        this.otherId = otherId;
        this.lastContent = lastContent;
        this.profileImage = profileImage;
        this.time = time;
    }

    public String getRoomName() {
        return roomName;
    }

    public ChattingRoomListDto setRoomName(String roomName) {
        this.roomName = roomName;
        return this;
    }

    public String getMyId() {
        return myId;
    }

    public ChattingRoomListDto setMyId(String myId) {
        this.myId = myId;
        return this;
    }

    public String getOtherId() {
        return otherId;
    }

    public ChattingRoomListDto setOtherId(String otherId) {
        this.otherId = otherId;
        return this;
    }

    public String getLastContent() {
        return lastContent;
    }

    public ChattingRoomListDto setLastContent(String lastContent) {
        this.lastContent = lastContent;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public ChattingRoomListDto setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getTime() {
        return time;
    }

    public ChattingRoomListDto setTime(String time) {
        this.time = time;
        return this;
    }
}
