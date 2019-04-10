package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {

    private String host;
    private String port;

    public Listener(String host, String port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))){
            while(true){
                Socket newSoket = serverSocket.accept();
                Connection connection = new Connection(newSoket, host);
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
