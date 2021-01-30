package dto;

public class OrderDto {

    private String image;
    private String pname;
    private String sizeAndColor;
    private int price;
    private int quantity;
    private String result;
    private String indate;


    @Override
    public String toString() {
        return "OrderDto{" +
                "image='" + image + '\'' +
                ", pname='" + pname + '\'' +
                ", sizeAndColor='" + sizeAndColor + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", result='" + result + '\'' +
                ", indate='" + indate + '\'' +
                '}';
    }

    public OrderDto() {}

    public OrderDto
            ( String image, String pname, String sizeAndColor, int price, int quantity, String result, String indate) {

        this.image = image;
        this.pname = pname;
        this.sizeAndColor = sizeAndColor;
        this.price = price;
        this.quantity = quantity;
        this.result = result;
        this.indate = indate;
    }


    public String getImage() {
        return image;
    }

    public OrderDto setImage(String image) {
        this.image = image;
        return this;
    }

    public String getPname() {
        return pname;
    }

    public OrderDto setPname(String pname) {
        this.pname = pname;
        return this;
    }

    public String getSizeAndColor() {
        return sizeAndColor;
    }

    public OrderDto setSizeAndColor(String sizeAndColor) {
        this.sizeAndColor = sizeAndColor;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public OrderDto setPrice(int price) {
        this.price = price;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderDto setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getResult() {
        return result;
    }

    public OrderDto setResult(String result) {
        this.result = result;
        return this;
    }

    public String getIndate() {
        return indate;
    }

    public OrderDto setIndate(String indate) {
        this.indate = indate;
        return this;
    }
}
