package dto;

public class QnaDto {

    private String email;
    private String subject;
    private String title;
    private String content;
    private String indate;

    public QnaDto() {}

    public QnaDto(String email, String subject, String title, String content, String indate) {
        this.email = email;
        this.subject = subject;
        this.title = title;
        this.content = content;
        this.indate = indate;
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
}
