package dto;

import java.io.Serializable;

//Serializable 를 상속해주면 intent 로 객체를 넘길 때 사용할 수 있다.
// : 객체를 전달하는 측과 해당 객체를 수신하는 측에서 사용하는 클래스 파일이 동일한지 체크하는 용도로 사용
public class UserDto implements Serializable {

    private String name;
    private String id;
    private String pw;
    private String email;
    private String phone;
    private int profilePhoto;
    private int point;

    public UserDto() {}

    public UserDto(String name, String id, String pw, String email, String phone, int profilePhoto, int point) {
        this.name = name;
        this.id = id;
        this.pw = pw;
        this.email = email;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.point = point;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(int profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
