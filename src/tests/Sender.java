package tests;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Sender extends Thread{

    public Sender() {

    }

    @Override
    public void run() {
        super.run();

        try(Socket senderSocket = new Socket()) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 5555);
            senderSocket.connect(inetSocketAddress);

            //starting writer and reader
            BufferedInputStream reader = new BufferedInputStream(senderSocket.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(senderSocket.getOutputStream());






        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
