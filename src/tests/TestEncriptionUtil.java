package tests;

import exceptions.TransferToolException;
import utils.EncryptionUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

public class TestEncriptionUtil {
    public static void main(String[] args) {

        RSAPublicKey publicKey = null;
        RSAPrivateKey privateKey = null;
        try{
            // generates the keys if not eists
            if (!EncryptionUtil.areKeysPresent()) EncryptionUtil.generateKey();

            //key file reader
            ObjectInputStream inputStream;

            // gets the public key
            inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PUBLIC_KEY_PATH));
            publicKey = (RSAPublicKey) inputStream.readObject();

            // gets the private key
            inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH));
            privateKey = (RSAPrivateKey) inputStream.readObject();

        }catch (Exception e){
            System.err.println("Certificates error");
            System.exit(-1);
        }

        byte[] encripted = new byte[0];
        try {
            encripted = EncryptionUtil.encryptString("cactus",publicKey);
        } catch (TransferToolException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(Arrays.toString(EncryptionUtil.decrypt(encripted, privateKey)));
        } catch (TransferToolException e) {
            e.printStackTrace();
        }


    }
}
