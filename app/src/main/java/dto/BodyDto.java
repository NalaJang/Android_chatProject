package dto;

public class BodyDto {

    private String id;
    private String shoulder;
    private String weight;

    public BodyDto() {}

    public BodyDto(String id, String shoulder, String weight) {
        this.id = id;
        this.shoulder = shoulder;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShoulder() {
        return shoulder;
    }

    public void setShoulder(String shoulder) {
        this.shoulder = shoulder;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
