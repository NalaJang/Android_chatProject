package dto;

public class PointDto {

    String id;
    String product;
    int point;
    String indate;

    public PointDto() {}

    public PointDto(String id, String product, int point, String indate) {
        this.id = id;
        this.product = product;
        this.point = point;
        this.indate = indate;
    }

    public String getId() {
        return id;
    }

    public PointDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public PointDto setProduct(String product) {
        this.product = product;
        return this;
    }

    public int getPoint() {
        return point;
    }

    public PointDto setPoint(int point) {
        this.point = point;
        return this;
    }

    public String getIndate() {
        return indate;
    }

    public PointDto setIndate(String indate) {
        this.indate = indate;
        return this;
    }
}
