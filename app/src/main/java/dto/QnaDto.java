package dto;

public class QnaDto {

    private String num;
    private String email;
    private String subject;
    private String title;
    private String content;
    private String indate;
    private String result;

    public QnaDto() {}

    public QnaDto(String num, String email, String subject, String title, String content, String indate, String result) {
        this.email = email;
        this.subject = subject;
        this.title = title;
        this.content = content;
        this.indate = indate;
        this.result = result;
    }

    public String getNum() {
        return num;
    }

    public QnaDto setNum(String num) {
        this.num = num;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public QnaDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public QnaDto setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public QnaDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public QnaDto setContent(String content) {
        this.content = content;
        return this;
    }

    public String getIndate() {
        return indate;
    }

    public QnaDto setIndate(String indate) {
        this.indate = indate;
        return this;
    }

    public String getResult() {
        return result;
    }

    public QnaDto setResult(String result) {
        this.result = result;
        return this;
    }
}
