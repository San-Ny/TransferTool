package sender;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SendService extends Thread {

    private Socket socket;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public SendService(Socket socket, PrivateKey privateKey, PublicKey publicKey) {
        this.socket = socket;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public void run() {
        super.run();


    }
}
