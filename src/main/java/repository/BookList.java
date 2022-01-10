package repository;

import entity.Book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class BookList {
    ArrayList<Book> booksList;

    public BookList(ArrayList<Book> booksList) {
        this.booksList = booksList;
    }

    public BookList() {
        this.booksList = new ArrayList<Book>();
    }

    @Override
    public String toString() {
        String output="";
        for (Book b: booksList){
            output+="Name: "+b.getName()+
                    "\nAuthor: "+b.getAuthorName()+
                    "\nGenre: "+b.getGenre()+
                    "\nDate: "+b.getPublishDate()+
                    "\nISBN: "+b.getIsbn()+"\n\n";

        }
        return output;
    }
}
