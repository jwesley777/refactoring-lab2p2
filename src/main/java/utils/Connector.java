package utils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connector {

    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;

    public Connector() {
        try {
            clientSocket = new Socket("localhost", 4004);
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String sendMessage(String data) {
        try {
            out.write(data + "\n");
            out.flush();
            String serverWord = in.readLine();
            //System.out.println(serverWord);
            return serverWord;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error getting message from server";

    }
}


