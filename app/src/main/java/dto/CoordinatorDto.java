package dto;

import java.io.Serializable;

public class CoordinatorDto implements Serializable {

    private String name;
    private String workerId;
    private String pw;
    private String email;
    private int profilePhoto;

    public CoordinatorDto() {}

    public CoordinatorDto(String name, String workerId, String pw, String email, int profilePhoto) {
        this.name = name;
        this.workerId = workerId;
        this.pw = pw;
        this.email = email;
        this.profilePhoto = profilePhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
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

    public int getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(int profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
