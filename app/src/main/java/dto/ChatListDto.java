package dto;

public class ChatListDto {

    private int num;
    private String workerId;
    private String content;


    public ChatListDto(){}

    public ChatListDto(int num, String workerId, String content) {
        this.num = num;
        this.workerId = workerId;
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public ChatListDto setNum(int num) {
        this.num = num;
        return this;
    }

    public String getWorkerId() {
        return workerId;
    }

    public ChatListDto setWorkerId(String workerId) {
        this.workerId = workerId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ChatListDto setContent(String content) {
        this.content = content;
        return this;
    }
}
