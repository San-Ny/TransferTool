package utils;

import exceptions.TransferToolException;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;
import java.util.Properties;

import static java.util.Objects.requireNonNull;
import static utils.ConsolePrinterUtil.*;

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

            // Try to create files, run tool as sudo to avoid security holes
            if (privateKeyFile.getParentFile() != null) if(privateKeyFile.getParentFile().mkdirs()) die("Unable to create directories in /etc/ permission needed", -1);

            if (!privateKeyFile.createNewFile()) die("Unable to write key to /etc/transfertool/keys, permission needed", -1);

            if (publicKeyFile.getParentFile() != null) if(publicKeyFile.getParentFile().mkdirs())  die("Unable to create directories in /etc/ permission needed", -1);
            if (!publicKeyFile.createNewFile()) die("Unable to write key to /etc/transfertool/keys, permission needed", -1);

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
    public static byte[] encryptString(String text, PublicKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPT);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return cipher");
    }

    public static byte[] encrypt(byte[] text, PublicKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPT);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return cipher");
    }

    public static byte[] encrypt(byte[] text, Cipher cipher) throws TransferToolException {
        try {
            return cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return cipher");
    }

    public static Cipher getEncryptionCipher(PublicKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPT);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return cipher");
    }

    public static Cipher getDecryptionCipher(PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return cipher;
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
    public static byte[] decryptString(String text, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return cipher.doFinal(text.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] text, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return cipher.doFinal(text);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void encryptFile(Properties properties){
        if (!EncryptionUtil.areKeysPresent()) EncryptionUtil.generateKey();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PUBLIC_KEY_PATH))) {
            RSAPublicKey publicKey = (RSAPublicKey) inputStream.readObject();
            if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
                while (true) {
                    String line = ScannerUtil.getLine("Text to encrypt (n to exit):");
                    if (line.equals("n")) die(0);
                    printByteEncrypted(requireNonNull(EncryptionUtil.encryptString(line, publicKey)));
                }
            } else {
                try {
                    File fileOut = new File(properties.get("fileLocal") +".encrypted");
                    File fileIn = new File((String)properties.get("fileLocal"));
                    DataOutputStream out = new DataOutputStream(new FileOutputStream(fileOut));
                    BufferedReader in = new BufferedReader(new FileReader(fileIn));
                    String line;
                    while ((line = in.readLine()) != null){
                        byte[] cipherText = EncryptionUtil.encryptString(line, publicKey);
                        out.writeInt(cipherText.length);
                        System.out.println(Arrays.toString(cipherText));
                        for (byte b: cipherText) out.writeByte((int)b);
                        out.flush();
                    }
                    in.close();
                    out.close();
                    die("File encrypted " + PathFinderUtil.getPathFileName(Path.of((String) properties.get("fileLocal"))), 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException | TransferToolException e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(Properties properties) {

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH))) {
            RSAPrivateKey privateKey = (RSAPrivateKey) inputStream.readObject();
            if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
                while (true) {
                    String line = ScannerUtil.getLine("Text to decrypt (n to exit):");
                    if (line.equals("n")) die(0);
                    println(new String(requireNonNull(EncryptionUtil.decryptString(line, privateKey)), StandardCharsets.UTF_8));
                }
            } else {
                try {
                    File fileOut = new File(properties.get("fileLocal") +".decrypted");
                    File fileIn = new File((String)properties.get("fileLocal"));
                    BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
                    DataInputStream in = new DataInputStream(new FileInputStream(fileIn));
                    while(in.available() != 0){
                        int a = in.readInt();
                        byte[] data = new byte[a];
                        for(int c = 0; c < a; c++) data[c] = in.readByte();
                        byte[] returned = EncryptionUtil.decrypt(data, privateKey);
                        assert returned != null;
                        for (int i:returned) out.write((char)i);
                        out.flush();
                        out.newLine();
                    }
                    in.close();
                    out.close();
                    die("File decrypted" + PathFinderUtil.getPathFileName(Path.of((String) properties.get("fileLocal"))), 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
