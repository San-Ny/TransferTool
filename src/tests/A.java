package tests;

import pojos.TransferToolPKey;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;

public class A  extends Thread{

    static RSAPublicKey pubkey;
    static RSAPrivateKey privkey;
    static TransferToolPKey ttpk;

    public A() {
        //second pair of keys
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024); // speedy generation, but not secure anymore
            KeyPair kp = kpg.generateKeyPair();
            pubkey = (RSAPublicKey) kp.getPublic();
            privkey = (RSAPrivateKey) kp.getPrivate();

            ttpk = new TransferToolPKey(pubkey);
        }catch (Exception e){
            e.printStackTrace();
        }
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

                    byte[] data = new byte[50];
                    reader.read(data);

                    System.out.println("A -> " + new String(data));

                    data = "okey".getBytes();
                    writer.write(data);
                    writer.flush();

                    //obteniendo clave publica
                    ObjectInputStream in = new ObjectInputStream(newSocket.getInputStream());
                    RSAPublicKey bKey = (RSAPublicKey) in.readObject();

                    System.out.println(bKey);

                    writer.write(encrypt("i hope you get cancer",bKey));
                    writer.flush();

                    System.out.println("A -> connected");
                }catch (Exception e){
                    System.err.println("transaction successfully failed");
                }
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

    public String decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            Cipher oaepFromInit = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            oaepFromInit.init(Cipher.DECRYPT_MODE, key, oaepParams);
            byte[] pt = oaepFromInit.doFinal(text);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(dectyptedText);
    }


    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
            Cipher oaepFromAlgo = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            oaepFromAlgo.init(Cipher.ENCRYPT_MODE, key);
            return oaepFromAlgo.doFinal(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }


}
