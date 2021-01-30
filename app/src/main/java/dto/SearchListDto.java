package dto;

public class SearchListDto {

    private String workerNum;
    private String workerId;
    private String workerContent;

    @Override
    public String toString() {
        return "SearchListDto{" +
                "workerNum='" + workerNum + '\'' +
                ", workerId='" + workerId + '\'' +
                ", workerContent='" + workerContent + '\'' +
                '}';
    }

    public SearchListDto() {}

    public SearchListDto(String workerNum, String workerId, String workerContent) {
        this.workerNum = workerNum;
        this.workerId = workerId;
        this.workerContent = workerContent;
    }

    public String getWorkerNum() {
        return workerNum;
    }

    public SearchListDto setWorkerNum(String workerNum) {
        this.workerNum = workerNum;
        return this;
    }

    public String getWorkerId() {
        return workerId;
    }

    public SearchListDto setWorkerId(String workerId) {
        this.workerId = workerId;
        return this;
    }

    public String getWorkerContent() {
        return workerContent;
    }

    public SearchListDto setWorkerContent(String workerContent) {
        this.workerContent = workerContent;
        return this;
    }
}
