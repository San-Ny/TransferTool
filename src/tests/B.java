package tests;

import pojos.FolderClass;
import pojos.TransferToolPKey;
import utils.ByteSerializer;
import utils.EncriptionUtil;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;

public class B extends Thread{

    static RSAPublicKey pubkey;
    static RSAPrivateKey privkey;
    static TransferToolPKey ttpk;

    public B() {
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

        try(Socket senderSocket = new Socket()) {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 5555);
            senderSocket.connect(inetSocketAddress);

            //starting writer and reader
            BufferedInputStream reader = new BufferedInputStream(senderSocket.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(senderSocket.getOutputStream());

            //get senderPKey bits
            byte[] senderPKey = ByteSerializer.serializeObject(new TransferToolPKey(pubkey));


            //send sender key to other pc
            writer.write(senderPKey);
            writer.flush();

            byte[] listenerPKeyBits = new byte[senderPKey.length];
            int result = reader.read(listenerPKeyBits);

            //transform the bits to object
            TransferToolPKey listenerPKey = null;
            try{
                listenerPKey = (TransferToolPKey) ByteSerializer.deserializeBytes(listenerPKeyBits);
            }catch (ClassNotFoundException e){
                System.err.println("class not found");
                System.exit(-1);
            }

            System.out.println("B pubkey -->:\n " + pubkey);
            System.out.println("B 'A' pubkey -->:\n" + listenerPKey.getPublicKey());




        }catch (IOException e){
            e.printStackTrace();
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

    private Cipher getCipherDecryptor(PrivateKey key){
        try {
            Cipher oaepFromInit = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            oaepFromInit.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return oaepFromInit;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Cipher getCipherEncryptor(PublicKey key){
        try {
            Cipher oaepFromAlgo = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            oaepFromAlgo.init(Cipher.ENCRYPT_MODE, key);
            return oaepFromAlgo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
