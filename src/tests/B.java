package tests;

import pojos.TransferToolPKey;
import utils.ByteSerializer;
import utils.EncriptionUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

            writer.write("buenas, entrando en tunel".getBytes());
            writer.flush();

            byte[] data = new byte[50];
            reader.read(data);

            System.out.println("B -> " + new String(data));

            //send publicKey
            ObjectOutputStream outO = new ObjectOutputStream(senderSocket.getOutputStream());
            outO.writeObject(pubkey);
            outO.flush();

            reader.read(data);
            System.out.println(decrypt(data, privkey));

            System.out.println("B -> connected");
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
