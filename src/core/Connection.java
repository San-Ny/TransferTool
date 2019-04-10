package core;

import java.io.*;
import java.net.Socket;

public class Connection extends Thread {

    private Socket socket;
    private String host;

    public Connection(Socket socket, String host){
        this.socket = socket;
        this.host = host;
    }

    @Override
    public void run() {
        super.run();

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer;
            if (!host.equals("")) writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){

        }
    }
}
