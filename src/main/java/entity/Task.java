package entity;


import lombok.Data;

@Data
public class Task {
    String type;
    Book book;
    String by;
    String value;

    String login;
    String password;


    public Task(String type, Book book) {
        this.type = type;
        this.book = book;
        this.by = null;
        this.value = null;

    }

    public Task(String type, String by, String value) {
        this.type = type;
        this.book = null;
        this.by = by;
        this.value = value;
    }

    public Task(String login, String password) {
        this.type = "login";
        this.login = login;
        this.password = password;
    }
}
