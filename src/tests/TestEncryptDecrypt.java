package tests;

import utils.EncryptionUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TestEncryptDecrypt {
    public static void main(String[] args) {

        try{
            // Check if the pair of keys are present else generate those.
            if (!EncryptionUtil.areKeysPresent()) {
                // Method generates a pair of keys using the RSA algorithm and stores it
                // in their respective files
                EncryptionUtil.generateKey();
            }

            final String originalText = "Text to be encrypted ";
            ObjectInputStream inputStream = null;

            // Encrypt the string using the public key
            inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PUBLIC_KEY_PATH));
            final PublicKey publicKey = (PublicKey) inputStream.readObject();
            final byte[] cipherText = EncryptionUtil.encrypt(originalText, publicKey);

            // Decrypt the cipher text using the private key.
            inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            final String plainText = EncryptionUtil.decrypt(cipherText, privateKey);

            // Printing the Original, Encrypted and Decrypted Text
            System.out.println("Original Text: " + originalText);
            System.out.println("Encrypted Text: " + cipherText.toString());
            System.out.println("Decrypted Text: " + plainText);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
