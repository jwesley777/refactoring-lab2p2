import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import entity.Book;
import entity.Response;
import entity.Task;
import repository.BookList;
import repository.BookRepository;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class Server {
    private static Socket clientSocket;
    private static ServerSocket server;
    private static BufferedReader in;
    private static BufferedWriter out;
    private static BookRepository bookList;
    public static void main(String[] args) {
        Connection conn = null;
        boolean loggedIn = false;

        try{
            String url = "jdbc:postgresql://localhost/rbdp2";
            Properties props = new Properties();
            props.setProperty("user","postgres");
            props.setProperty("password","postgres");
            props.setProperty("port","5432");
            conn = DriverManager.getConnection(url, props);

            bookList = new BookRepository(conn);

        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }



        try {
            server = new ServerSocket(4004);

        System.out.println("Server is up!");
        while (true) {
            try {
            if (!loggedIn) {
                clientSocket = server.accept();
                loggedIn = true;
            }
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String input = in.readLine();
            Gson gson = new Gson();
            Task task = gson.fromJson(input, Task.class);
            System.out.println("Task received: "+input);

            Response response;
            BookList responseBookList=null;

            if (task==null) {
                response=new Response("Fatal Error: task is null");
            }else if (task.getType().equals("add")) {
                bookList.addBook(task.getBook());
                response=new Response("Success");
            }else if (task.getType().equals("find")) {
                try {
                    responseBookList = bookList.findBy(task.getBy(), task.getValue());
                    response = new Response("Success");
                } catch (Exception e) {
                    e.printStackTrace();
                    response = new Response("Error: Exception during findBy operation");
                }
            } else if (task.getType().equals("login")) {
                String query = String.format("SELECT LOGIN, PASSWORD FROM USERS WHERE LOGIN = '%1$s'", task.getLogin());
                System.out.println(query);
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        String password = rs.getString("PASSWORD");
                        if (password.equals(task.getPassword())) {
                            response = new Response("ok");
                        } else {
                            response = new Response("incorrect login-password");
                        }
                    } else {
                        query = String.format("INSERT INTO USERS (LOGIN, PASSWORD) VALUES ('%1$s', '%2$s')",
                                task.getLogin(),
                                task.getPassword());
                        System.out.println(query);
                        stmt.executeUpdate(query);
                        response = new Response("New user is registered");
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    response = new Response("sql exception");
                }
            } else {
                response=new Response("Error: Unknown Method");
            }

            String message;
            if (responseBookList!=null) message = gson.toJson(responseBookList);
            else message=gson.toJson(response);


            System.out.println(message);
            out.write(message+ "\n");
            out.flush();

            Gson dump_gson = new GsonBuilder().setPrettyPrinting().create();

            System.out.println("request handling done");


            } catch (SocketException e) {
                loggedIn = false;
                System.out.println("client disconnected");
            }
        }
        } catch (IOException e) {
            try {
                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


            e.printStackTrace();
        }


    }

}

