package dto;

public class RoomList {

    private int user_img;
    private String room_name;
    private String last_content;
    private String last_time;

    public RoomList() {}

    public RoomList(int user_img, String room_name, String last_content, String last_time) {
        this.user_img = user_img;
        this.room_name = room_name;
        this.last_content = last_content;
        this.last_time = last_time;
    }

    public int getUser_img() {
        return user_img;
    }

    public void setUser_img(int user_img) {
        this.user_img = user_img;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getLast_content() {
        return last_content;
    }

    public void setLast_content(String last_content) {
        this.last_content = last_content;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }
}
