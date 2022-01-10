package entity;

import lombok.Data;

@Data
public class Book {
    private String name;
    private String authorName;
    private String genre;
    private String publishDate;
    private String annotation;
    private String isbn;

    public Book(String name, String authorName, String genre, String publishDate, String annotation, String isbn) {
        this.name = name;
        this.authorName = authorName;
        this.genre = genre;
        this.publishDate = publishDate;
        this.annotation = annotation;
        this.isbn = isbn;
    }
    public Book() {
        this.name = null;
        this.authorName = null;
        this.genre = null;
        this.publishDate = null;
        this.annotation = null;
        this.isbn = null;
    }

}
