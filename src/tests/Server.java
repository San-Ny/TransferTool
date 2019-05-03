package tests;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{


    public Server() {
    }

    @Override
    public void run() {
        super.run();
        try(ServerSocket serverSocket = new ServerSocket(5555)){
            while(true){
                Socket newSocket = serverSocket.accept();
                try{
                    BufferedInputStream reader = new BufferedInputStream(newSocket.getInputStream());
                    BufferedOutputStream writer = new BufferedOutputStream(newSocket.getOutputStream());

                    //do something

                }catch (Exception e){
                    System.err.println("transaction successfully failed");
                    System.exit(-1);
                }
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
