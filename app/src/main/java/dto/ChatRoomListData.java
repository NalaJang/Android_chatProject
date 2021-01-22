package dto;

public class ChatRoomListData {

    private String roomName;
    private String lastContent;
    private String profileImage;
    private String time;

    public ChatRoomListData() {}

    public ChatRoomListData(String roomName, String lastContent, String profileImage, String time) {
        this.roomName = roomName;
        this.lastContent = lastContent;
        this.profileImage = profileImage;
        this.time = time;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getLastContent() {
        return lastContent;
    }

    public void setLastContent(String lastContent) {
        this.lastContent = lastContent;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
