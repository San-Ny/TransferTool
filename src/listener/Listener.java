package listener;

import pojos.EncriptionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Listener {
    public static void main(String[] args) {
        //-p port

        if (args.length > 3) System.err.println("Unexpected arguments");

        String port = "9555";

        for (int a = 0; a < args.length; a++) if (args[a].equals("-p")) port = args[a + 1];

        //certificates

        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        try{
            // generates the keys if not eists
            if (!EncriptionUtil.areKeysPresent()) EncriptionUtil.generateKey();

            //key file reader
            ObjectInputStream inputStream;

            // gets the public key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PUBLIC_KEY_FILE));
            publicKey = (PublicKey) inputStream.readObject();

            // gets the private key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PRIVATE_KEY_FILE));
            privateKey = (PrivateKey) inputStream.readObject();

        }catch (Exception e){
            System.err.println("Certificates error");
            System.exit(-1);
        }

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))){
            while(true){
                Socket newSocket = serverSocket.accept();
                ListenService listenService = new ListenService(newSocket, privateKey, publicKey);
                listenService.start();
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
