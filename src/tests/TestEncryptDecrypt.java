package tests;

import utils.EncryptionUtil;

import java.io.*;
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
            final byte[] cipherText = EncryptionUtil.encryptString(originalText, publicKey);

            //file write
            DataOutputStream fout = new DataOutputStream(new FileOutputStream("/home/grdar/Downloads/encryptedText.txt"));
            fout.writeInt(cipherText.length);
            for (byte b: cipherText) fout.writeByte((int)b);
            fout.close();

            // Decrypt the cipher text using the private key.
            inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH));
            final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
            final byte[] plainText = EncryptionUtil.decrypt(cipherText, privateKey);

            DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("/home/grdar/Downloads/encryptedText.txt")));
            int a = in.readInt();
            byte[] retorn = new byte[a];
            for(int c = 0; c < a; c++) retorn[c] = in.readByte();
            in.close();
            final byte[] retornArxiu = EncryptionUtil.decrypt(retorn, privateKey);
            for (int i:retornArxiu) System.err.print((char)i);

            // Printing the Original, Encrypted and Decrypted Text
            System.out.println("Original Text: " + originalText);
            System.out.print("Encrypted Text: ");
            for (int i : cipherText) System.out.print((char)i);
            System.out.println();
            System.out.print("Decrypted Text: ");
            assert plainText != null;
            for (int i : plainText) System.out.print((char)i);
            System.out.println();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
