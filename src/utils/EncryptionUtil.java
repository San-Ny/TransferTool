package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;

public class EncryptionUtil {

    /**
     * variables from configUtil
     */
    private static final String ALGORITHM_KEY_PAR_GENERATOR = ConfigurationUtil.getPropertyOrDefault("AlgorithmKeyParGenerator", "RSA");
    private static final String ALGORITHM_ENCRYPT = ConfigurationUtil.getPropertyOrDefault("AlgorithmEncrypt", "RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
    private static final String ALGORITHM_DECRYPT = ConfigurationUtil.getPropertyOrDefault("AlgorithmDecrypt", "RSA/ECB/OAEPPadding");

    public static final String PRIVATE_KEY_PATH = ConfigurationUtil.getPropertyOrDefault("PrivateKeyPath", "/etc/transfertool/keys/private.key");
    public static final String PUBLIC_KEY_PATH = ConfigurationUtil.getPropertyOrDefault("PublicKeyPath", "/etc/transfertool/keys/public.key");

    /**
     * Saves a key pair of private and public key using 2048 bytes as default on configUtil path
     */
    public static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM_KEY_PAR_GENERATOR);
            keyGen.initialize(Integer.parseInt(ConfigurationUtil.getPropertyOrDefault("KeyBytes", "2048")));
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(PRIVATE_KEY_PATH);
            File publicKeyFile = new File(PUBLIC_KEY_PATH);

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
     * Check if are files on private and public file paths
     * @return flag indicating if the pair of keys exists
     */
    public static boolean areKeysPresent() {
        return new File(PRIVATE_KEY_PATH).exists() && new File(PUBLIC_KEY_PATH).exists();
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param text : original plain text
     * @param key : The public key
     * @return Encrypted text
     */
    public static byte[] encrypt(String text, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPT);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text :encrypted text
     * @param key :The private key
     * @return plain text
     */
    public static String decrypt(byte[] text, PrivateKey key) {
        try { //ConfigurationUtil.getPropertyOrDefault("", "")
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            byte[] pt = cipher.doFinal(text);
            return new String(pt, StandardCharsets.UTF_8);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
