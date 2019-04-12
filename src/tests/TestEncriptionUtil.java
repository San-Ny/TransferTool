package tests;

import utils.EncriptionUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class TestEncriptionUtil {
    public static void main(String[] args) {

        RSAPublicKey publicKey = null;
        RSAPrivateKey privateKey = null;
        try{
            // generates the keys if not eists
            if (!EncriptionUtil.areKeysPresent()) EncriptionUtil.generateKey();

            //key file reader
            ObjectInputStream inputStream;

            // gets the public key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PUBLIC_KEY_FILE));
            publicKey = (RSAPublicKey) inputStream.readObject();

            // gets the private key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PRIVATE_KEY_FILE));
            privateKey = (RSAPrivateKey) inputStream.readObject();

        }catch (Exception e){
            System.err.println("Certificates error");
            System.exit(-1);
        }

        byte[] encripted = EncriptionUtil.encrypt("cactus",publicKey);

        System.out.println(EncriptionUtil.decrypt(encripted, privateKey));

    }
}
