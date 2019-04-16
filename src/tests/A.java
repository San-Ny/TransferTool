package tests;

import pojos.FolderClass;
import pojos.TransferToolPKey;
import utils.ByteSerializer;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Properties;

public class A  extends Thread{

    static RSAPublicKey pubkey;
    static RSAPrivateKey privkey;
    static TransferToolPKey ttpk;
    static KeyFactory fact;

    static PublicKey publicKey;
    static PublicKey bPublicKey;
    static PrivateKey privateKey;

    public A() {
        //second pair of keys
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024); // speedy generation, but not secure anymore
            KeyPair kp = kpg.generateKeyPair();
//            pubkey = (RSAPublicKey) kp.getPublic();
//            privkey = (RSAPrivateKey) kp.getPrivate();
            fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(),
                    RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(),
                    RSAPrivateKeySpec.class);

            publicKey = fact.generatePublic(pub);
            privateKey = fact.generatePrivate(priv);


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
                TransferToolPKey senderPKey = null;
                try{
                    BufferedInputStream reader = new BufferedInputStream(newSocket.getInputStream());
                    BufferedOutputStream writer = new BufferedOutputStream(newSocket.getOutputStream());


                    //get the sender key
                    byte[] senderPKeyBits = new byte[1000];
                    reader.read(senderPKeyBits);

                    //transform the bits to object
                    senderPKey = (TransferToolPKey) ByteSerializer.deserializeBytes(senderPKeyBits);

                    //create our TransferToolPKey to send to sender
                    TransferToolPKey listenerPKey = new TransferToolPKey(pubkey);

                    //send our TransferToolPKey
                    writer.write(ByteSerializer.serializeObject(listenerPKey));
                    writer.flush();




                }catch (Exception e){
                    System.err.println("transaction successfully failed");
                    System.exit(-1);
                }

                System.out.println("A pubkey -->:\n " + pubkey);
                System.out.println("A 'B' pubkey -->:\n" + senderPKey.getPublicKey());




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

    public byte[] rsaEncrypt(byte[] data, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] rsaDecrypt(byte[] data, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
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
