package tests;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PostListenner {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8085);
            Socket socket = serverSocket.accept();
            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
            int content;
            StringBuilder sb = new StringBuilder();
            while ((content = in.read()) != -1) {
                // convert to char and display it
                sb.append((char) content);
            }
            in.close();
            System.out.println("Message: " + sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
