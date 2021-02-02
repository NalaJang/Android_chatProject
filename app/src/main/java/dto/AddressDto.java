package dto;

public class AddressDto {

    private String no;
    private String nickName;
    private String userName;
    private String id;
    private String phone;
    private String zip_num;
    private String address1;
    private String address2;
    private String result;

    @Override
    public String toString() {
        return "AddressDto{" +
                "no=" + no +
                ", nickName='" + nickName + '\'' +
                ", userName='" + userName + '\'' +
                ", id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", zip_num='" + zip_num + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public AddressDto() {}

    public AddressDto(String no, String nickName, String userName, String id, String phone, String zip_num, String address1, String address2, String result) {
        this.nickName = nickName;
        this.userName = userName;
        this.id = id;
        this.phone = phone;
        this.zip_num = zip_num;
        this.address1 = address1;
        this.address2 = address2;
        this.result = result;
    }

    public String getNo() {
        return no;
    }

    public AddressDto setNo(String no) {
        this.no = no;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public AddressDto setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AddressDto setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getId() {
        return id;
    }

    public AddressDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public AddressDto setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getZip_num() {
        return zip_num;
    }

    public AddressDto setZip_num(String zip_num) {
        this.zip_num = zip_num;
        return this;
    }

    public String getAddress1() {
        return address1;
    }

    public AddressDto setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public String getAddress2() {
        return address2;
    }

    public AddressDto setAddress2(String address2) {
        this.address2 = address2;
        return this;
    }

    public String getResult() {
        return result;
    }

    public AddressDto setResult(String result) {
        this.result = result;
        return this;
    }
}
