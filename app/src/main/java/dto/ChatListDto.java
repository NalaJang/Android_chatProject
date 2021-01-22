package dto;

public class ChatListDto {

    private String workerId;
    private String content;

    public ChatListDto(){}

    public ChatListDto(String workerId, String content) {
        this.workerId = workerId;
        this.content = content;
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
