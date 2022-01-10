package repository;

import entity.Book;
import entity.State;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BookRepository {
    Connection connection;

    public BookRepository(Connection connection) {
        this.connection = connection;
    }

    private int comparatorName(Book b1, Book b2, String value){
        String a1=b1.getName();
        String a2=b2.getName();
        return sortBySubstringCount(value, a1, a2);
    }
    private int comparatorAuthorName(Book b1,Book b2,String value){
        String a1=b1.getAuthorName();
        String a2=b2.getAuthorName();
        return sortBySubstringCount(value, a1, a2);
    }
    private int comparatorIsbn(Book b1,Book b2,String value) {
        String a1=b1.getIsbn();
        String a2=b2.getIsbn();
        return sortBySubstringCount(value, a1, a2);
    }

    private int sortBySubstringCount(String value, String a1, String a2) {
        int v1=(a1.length()-a1.replace(value,"").length())/value.length();
        int v2=(a2.length()-a2.replace(value,"").length())/value.length();
        if (v1>v2) return -1;
        else if (v1<v2) return 1;
        else return 0;
    }

    private int comparatorAnnotation(Book b1,Book b2,String value) {
        String a1=b1.getAnnotation();
        String a2=b2.getAnnotation();
        return sortBySubstringCount(value, a1, a2);
    }

    public BookList findBy(String by,String value) throws Exception {
        ArrayList<Book> sortedBooksList = queryWhere(by, value);

        if (by.equals("name")){

            sortedBooksList.sort((b1, b2) -> this.comparatorName(b1,b2,value));

        } else if (by.equals("isbn")){
            sortedBooksList.sort((b1, b2) -> this.comparatorIsbn(b1,b2,value));

        } else if (by.equals("authorName")){
            sortedBooksList.sort((b1, b2) -> this.comparatorAuthorName(b1,b2,value));

        } else if (by.equals("annotation")){
            sortedBooksList.sort((b1, b2) -> this.comparatorAnnotation(b1,b2,value));

        } else throw new Exception("Wrong by parameter");


        return new BookList(sortedBooksList);
    }



    public void addBook(Book book){
        String insertQuery = "INSERT INTO BOOKS (NAME, AUTHOR_NAME, GENRE, PUBLISH_DATE, ANNOTATION, ISBN) " +
                String.format("VALUES ('%1$s', '%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
                        book.getName(), book.getAuthorName(), book.getGenre(),
                        book.getPublishDate(), book.getAnnotation(), book.getIsbn());
        System.out.println(insertQuery);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(insertQuery);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public ArrayList<Book> queryWhere(String column, String value) throws SQLException {
        String query = "SELECT NAME, AUTHOR_NAME, GENRE, PUBLISH_DATE, ANNOTATION, ISBN FROM BOOKS" +
                " WHERE " + column + " LIKE '%" + value + "%'";
        System.out.println(query);
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            ArrayList<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("NAME"),
                        rs.getString("AUTHOR_NAME"),
                        rs.getString("GENRE"),
                        rs.getString("PUBLISH_DATE"),
                        rs.getString("ANNOTATION"),
                        rs.getString("ISBN")
                        );
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        String query = "SELECT NAME, AUTHOR_NAME, GENRE, PUBLISH_DATE, ANNOTATION, ISBN FROM BOOKS";;
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            ArrayList<Book> booksList = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("NAME"),
                        rs.getString("AUTHOR_NAME"),
                        rs.getString("GENRE"),
                        rs.getString("PUBLISH_DATE"),
                        rs.getString("ANNOTATION"),
                        rs.getString("ISBN")
                );
                booksList.add(book);

                String output="";
                for (Book b: booksList){
                    output += "Name: "+b.getName()+
                            "\nAuthor: "+b.getAuthorName()+
                            "\nGenre: "+b.getGenre()+
                            "\nDate: "+b.getPublishDate()+
                            "\nISBN: "+b.getIsbn()+"\n\n";

                }
                return output;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;


    }
}
