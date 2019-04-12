package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;

/**
 * @author JavaDigest
 *
 */
public class EncriptionUtil {
    /**
     * String to hold name of the encryption algorithm.
     * keys can be stored in software, create a static final variable for PublicKey and PrivateKey (instead of String) and use it.
     */
    public static final String ALGORITHMKEYPARGENERATOR = "RSA";
    public static final String ALGORITHMENCRIPTOR = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static final String ALGORITHMDECRIPTOR = "RSA/ECB/OAEPPadding";

    /**
     * String to hold the name of the private key file.
     */
    public static final String PRIVATE_KEY_FILE = "/etc/transfertool/private.key";

    /**
     * String to hold name of the public key file.
     */
    public static final String PUBLIC_KEY_FILE = "/etc/transfertool/public.key";

    /**
     * Generate key which contains a pair of private and public key using 1024
     * bytes. Store the set of keys in Prvate.key and Public.key files.
     *
     * @throws NoSuchAlgorithmException exception
     * @throws IOException exception
     * @throws FileNotFoundException exception
     */
    public static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHMKEYPARGENERATOR);
            keyGen.initialize(2048); //2048 - 1024
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(PRIVATE_KEY_FILE);
            File publicKeyFile = new File(PUBLIC_KEY_FILE);

            // Create files to store public and private key
            if (privateKeyFile.getParentFile() != null) {
                privateKeyFile.getParentFile().mkdirs();
            }
            privateKeyFile.createNewFile();

            if (publicKeyFile.getParentFile() != null) {
                publicKeyFile.getParentFile().mkdirs();
            }
            publicKeyFile.createNewFile();

            // Saving the Public key in a file
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(key.getPublic());
            publicKeyOS.close();

            // Saving the Private key in a file
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(key.getPrivate());
            privateKeyOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * The method checks if the pair of public and private key has been generated.
     *
     * @return flag indicating if the pair of keys were generated.
     */
    public static boolean areKeysPresent() {

        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        return privateKey.exists() && publicKey.exists();
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text
     *          : original plain text
     * @param key
     *          :The public key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public static byte[] encrypt(String text, PublicKey key) {
        byte[] cipherText = null;
        try {
//            // get an RSA cipher object and print the provider
//            final Cipher cipher = Cipher.getInstance(ALGORITHMENCRIPTOR);
//            // encrypt the plain text using the public key
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            cipherText = cipher.doFinal(text.getBytes());


            Cipher oaepFromAlgo = Cipher.getInstance(ALGORITHMENCRIPTOR);
            oaepFromAlgo.init(Cipher.ENCRYPT_MODE, key);
            return oaepFromAlgo.doFinal(text.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text
     *          :encrypted text
     * @param key
     *          :The private key
     * @return plain text
     * @throws java.lang.Exception
     */
    public static String decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
//            // get an RSA cipher object and print the provider
//            final Cipher cipher = Cipher.getInstance(ALGORITHMDECRIPTOR);
//
//            // decrypt the text using the private key
//            cipher.init(Cipher.DECRYPT_MODE, key);
//            dectyptedText = cipher.doFinal(text);


            Cipher oaepFromInit = Cipher.getInstance(ALGORITHMDECRIPTOR);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            oaepFromInit.init(Cipher.DECRYPT_MODE, key, oaepParams);
            byte[] pt = oaepFromInit.doFinal(text);
            return new String(pt, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }
}
