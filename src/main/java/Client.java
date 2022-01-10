import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.*;
import repository.BookList;
import utils.Connector;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {

    int state = State.IDLE;
    String currentBy = null;
    Book currentBook = new Book();
    Connector client = new Connector();


    private String sendTask(Task task){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String message=gson.toJson(task);
        String response = client.sendMessage(message);
        return response;
    }

    private int addBook() {
        Task task=new Task("add",this.currentBook);
        String response=this.sendTask(task);
        if (response==null) return -1;
        return 0;
    }

    private String getFindCollection(String value) {
        Task task=new Task("find",this.currentBy,value);
        String response=this.sendTask(task);
        return response;
    }


    private void handleIdle(String input) {
        this.currentBy = null;
        this.currentBook = new Book();
        switch (input) {
            case ("1"): {
                this.state = State.INPUT_NAME;
                break;
            }
            case ("2"): {
                this.state = State.FIND;
                break;
            }
            case ("EXIT"): {
                this.state = State.EXIT;
                break;
            }
            default: System.out.println("Incorrect input");
        }
    }

    private void handleInputName(String input) {
        switch (input) {
            case ("EXIT"): {
                this.state = State.IDLE;
                break;
            }
            default: {
                this.currentBook.setName(input);
                this.state=State.INPUT_AUTHORNAME;
            }
        }

    }
    private void handleInputAuthorName(String input) {
        switch (input) {
            case ("EXIT"): {
                this.state = State.IDLE;
                break;
            }
            default: {
                this.currentBook.setAuthorName(input);
                this.state=State.INPUT_GENRE;
            }
        }

    }
    private void handleInputGenre(String input) {
        switch (input) {
            case ("EXIT"): {
                this.state = State.IDLE;
                break;
            }
            default: {
                this.currentBook.setGenre(input);
                this.state=State.INPUT_PUBLISHDATE;
            }
        }
    }
    private void handleInputPublishDate(String input) {
        switch (input) {
            case ("EXIT"): {
                this.state = State.IDLE;
                break;
            }
            default: {
                this.currentBook.setPublishDate(input);
                this.state=State.INPUT_ANNOTATION;
            }
        }

    }
    private void handleInputAnnotation(String input) {
        switch (input) {
            case ("EXIT"): {
                this.state = State.IDLE;
                break;
            }
            default: {
                this.currentBook.setAnnotation(input);
                this.state = State.INPUT_ISBN;
            }
        }
    }
    private void handleInputIsbn(String input) {
        switch (input) {
            case ("EXIT"): {
                this.state = State.IDLE;
                break;
            }
            default: {
                this.currentBook.setIsbn(input);
                int result=this.addBook();

                if (result==0) System.out.println("Book is added to collection");
                else System.out.println("Book is not added to collection due to error");

                this.state=State.IDLE;

            }
        }
    }



    private void handleFind(String input) {
        switch (input) {
            case ("1") : {
                this.state=State.FIND_VALUE;
                this.currentBy ="name";
                break;
            }
            case ("2") : {
                this.state=State.FIND_VALUE;
                this.currentBy ="authorName";
                break;
            }
            case ("3") : {
                this.state=State.FIND_VALUE;
                this.currentBy ="isbn";
                break;
            }
            case ("4") : {
                this.state=State.FIND_VALUE;
                this.currentBy ="annotation";
                break;
            }
            case ("EXIT") : {
                this.state = State.IDLE;
                break;
            }
            default : System.out.println("Incorrect input");
        }
    }

    private void handleFindValue(String input) {
        switch (input) {
            case ("EXIT") : this.state = State.IDLE;
            default : {
                String result = this.getFindCollection(input);
                System.out.println("Search results:\n");
                Gson gson = new Gson();
                try {
                    BookList responseBookList = gson.fromJson(result, BookList.class);
                    System.out.println(responseBookList);
                } catch (Exception e) {
                    Response response = gson.fromJson(result, Response.class);
                    System.out.println(response);


                }
                this.state = State.IDLE;
            }
        }
    }

    private void handleExit(String input) {
        switch (input) {
            case ("y") : System.exit(0);
            case ("n") : this.state = State.IDLE;
            default : System.out.println("Do you want to exit? (enter: y/n)");
        }
    }



    public void handle(String input) {
        switch (this.state) {
            case (State.IDLE) : {
                this.handleIdle(input);
                break;
            }
            case (State.INPUT_NAME) : {
                this.handleInputName(input);
                break;
            }
            case (State.INPUT_AUTHORNAME) : {
                this.handleInputAuthorName(input);
                break;
            }
            case (State.INPUT_GENRE) : {
                this.handleInputGenre(input);
                break;
            }
            case (State.INPUT_PUBLISHDATE) : {
                this.handleInputPublishDate(input);
                break;
            }
            case (State.INPUT_ANNOTATION) : {
                this.handleInputAnnotation(input);
                break;
            }
            case (State.INPUT_ISBN) : {
                this.handleInputIsbn(input);
                break;
            }
            case (State.FIND) : {
                this.handleFind(input);
                break;
            }
            case (State.FIND_VALUE) : {
                this.handleFindValue(input);
                break;
            }
            case (State.EXIT) : {
                this.handleExit(input);
                break;
            }
            default : throw new IllegalStateException("Unexpected value: " + this.state);
        }

    }

    public void print() {
        switch (this.state) {
            case (State.IDLE) : {
                System.out.println("Type '1' to add new book\nType '2' to find the book\nType 'EXIT' to exit");
                break;
            }

            case (State.INPUT_NAME) : {
                System.out.println("Enter book name...\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.INPUT_AUTHORNAME) : {
                System.out.println("Enter author name...\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.INPUT_GENRE) : {
                System.out.println("Enter genre name...\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.INPUT_PUBLISHDATE) : {
                System.out.println("Enter date of publication...\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.INPUT_ISBN) : {
                System.out.println("Enter isbn...\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.INPUT_ANNOTATION) : {
                System.out.println("Enter annotation...\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.FIND) : {
                System.out.println("Type '1' to search by name\nType '2' to search by author\nType '3' to search by isbn\nType '4' to search by key words\nType 'EXIT' to return to main menu");
                break;
            }
            case (State.FIND_VALUE) : {
                switch (this.currentBy){
                    case ("name") : {
                        System.out.println("Enter book name:" + "\nType 'EXIT' to return to main menu");
                        break;
                    }
                    case ("authorName") : {
                        System.out.println("Enter author name:" + "\nType 'EXIT' to return to main menu");
                        break;
                    }
                    case ("isbn") : {
                        System.out.println("Enter isbn:" + "\nType 'EXIT' to return to main menu");
                        break;
                    }
                    case ("annotation") : {
                        System.out.println("Enter key words:" + "\nType 'EXIT' to return to main menu");
                        break;
                    }
                    default : throw new IllegalStateException("Unexpected by parameter: " + this.currentBy);

                }
                break;

            }
            case (State.EXIT) : {
                System.out.println("Do you want to exit? (y/n)");
                break;
            }

            default : throw new IllegalStateException("Unexpected state: " + this.state);
        }
    }

    public static void main(String[] args) {
        Client app=new Client();
        Scanner scanner = new Scanner(System.in);

        // login
        System.out.println("type login...");
        String login = scanner.next();
        System.out.println("type password...");
        String password = scanner.next();
        byte[] hashedPassword;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashedPassword.length; i++) {
                sb.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            password = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }

        // send login message
        Task loginMessage = new Task(login, password);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String message=gson.toJson(loginMessage);
        String responseMessage = app.client.sendMessage(message);

        Response response = gson.fromJson(responseMessage, Response.class);
        if (response.getMessage().equals("ok")) {
            System.out.println("Logged in successfully");
        } else if (response.getMessage().equals("New user is registered")) {
            System.out.println("New user is registered");
        } else {
            System.out.println("Wrong login-password pair. Exiting...");
            System.exit(1);
        }

        app.print();
        while(scanner.hasNext()){
            app.handle(scanner.next());
            app.print();
            System.out.print("> ");

        }
    }
}
