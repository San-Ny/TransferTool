package tests;

import utils.ConfigurationUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Properties;

public class Server extends Thread{

    private Properties properties;


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

//    RSAPublicKey publicKey = null;
//    RSAPrivateKey privateKey = null;
//        try{
//                // generates the keys if not eists
//                if (!EncriptionUtil.areKeysPresent()) EncriptionUtil.generateKey();
//
//                //key file reader
//                ObjectInputStream inputStream;
//
//                // gets the public key
//                inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PUBLIC_KEY_FILE));
//                publicKey = (RSAPublicKey) inputStream.readObject();
//
//                // gets the private key
//                inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PRIVATE_KEY_FILE));
//                privateKey = (RSAPrivateKey) inputStream.readObject();
//
//                }catch (Exception e){
//                System.err.println("Certificates error");
//                System.exit(-1);
//                }
