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
import java.util.Properties;

import static java.util.Objects.requireNonNull;
import static utils.ConsolePrinterUtil.*;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class EncryptionUtil {

    /**
     * variables from configUtil or default
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
        throw new TransferToolException("Unable to return encrypted data");
    }

    /**
     * encrypt data with public key
     * @param bytes data as byte array
     * @param key public key to encrypt
     * @return byte array with encrypted data
     * @throws TransferToolException when bytes can't be encrypted
     */
    public static byte[] encrypt(byte[] bytes, PublicKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_ENCRYPT);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return encrypted data");
    }

    /**
     * encrypt with custom cipher
     * @param bytes data to encrypt
     * @param cipher cipher pre initialized
     * @return encrypted data
     * @throws TransferToolException Unable to return encrypted data
     */
    public static byte[] encrypt(byte[] bytes, Cipher cipher) throws TransferToolException {
        try {
            return cipher.doFinal(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return encrypted data");
    }

    /**
     * return initialized cipher with Config.conf values as ENCRYPT_MODE
     * @param key public key to init cipher
     * @return cipher initialized with argument public key in ENCRYPT_MODE
     * @throws TransferToolException Unable to return cipher
     */
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

    /**
     * return initialized cipher with Config.conf values as DECRYPT_MODE
     * @param key private key to init cipher
     * @return cipher initialized with argument private key in DECRYPT_MODE
     * @throws TransferToolException Unable to return cipher
     */
    public static Cipher getDecryptionCipher(PrivateKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return cipher;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new TransferToolException("Unable to return cipher");
    }

    /**
     * Decrypt text using private key.
     * @param text :encrypted text
     * @param key :The private key
     * @return decrypted data
     * @throws TransferToolException Unable to return decrypted string
     */
    public static byte[] decryptString(String text, PrivateKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return cipher.doFinal(text.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new TransferToolException("Unable to return decrypted string");
    }

    /**
     * return decrypted data
     * @param bytes array of values
     * @param key private key to init cipher
     * @return decrypted data
     * @throws TransferToolException Unable to return decrypted data
     */
    public static byte[] decrypt(byte[] bytes, PrivateKey key) throws TransferToolException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DECRYPT);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(ConfigurationUtil.getPropertyOrDefault("OAEPParameterMdName", "SHA-256"), ConfigurationUtil.getPropertyOrDefault("OAEPParameterMgfName", "MGF1"), new MGF1ParameterSpec(ConfigurationUtil.getPropertyOrDefault("MGF1ParameterMdName", "SHA-1")), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, oaepParams);
            return cipher.doFinal(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        throw new TransferToolException("Unable to return decrypted data");
    }

    /**
     * read an console input to generate an array byte of encrypted data or read a path to generate an encrypted file
     * @param properties properties with required parameters
     */
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


    /**
     * read console input trying to parse it into an byte array to return a decrypted text or read a path to write a decrypted file
     * @param properties properties with required parameters
     */
    public static void decryptFile(Properties properties) {

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH))) {
            RSAPrivateKey privateKey = (RSAPrivateKey) inputStream.readObject();
            if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
                while (true) {
                    String line = ScannerUtil.getLine("Array to decrypt (n to exit):");
                    if (line.equals("n")) die(0);
                    String[] unparsedData = line.trim().replace("[", "").replace("]", "").split("[,]");
                    byte[] data = new byte[unparsedData.length];
                    for(int c = 0; c < data.length; c++) data[c] = Byte.valueOf(unparsedData[c].trim());
                    println(new String(requireNonNull(EncryptionUtil.decrypt(data, privateKey)), StandardCharsets.UTF_8));
                }
            } else {
                try {
                    File fileOut = new File(PathFinderUtil.removeExtension((String)properties.get("fileLocal")) + ".decrypted.txt");
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
                } catch (IOException | TransferToolException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException | TransferToolException e) {
            e.printStackTrace();
        }
    }
}
