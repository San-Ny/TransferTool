package main;

import exceptions.TransferToolException;
import parallelshell.ParallelSessionController;
import exceptions.WrongArgumentException;
import secureshell.SecureShell;
import sftpsender.SFTPSender;
import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.EncryptionUtil;
import utils.PathFinderUtil;
import utils.ScannerUtil;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.nio.file.Path;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.Properties;

import static utils.ArgumentReaderUtil.isNotValid;
import static utils.ArgumentReaderUtil.isOneValid;
import static utils.ConfigurationUtil.*;
import static utils.ConsolePrinterUtil.*;

public class Main {
    public static void main(String[] args) {

        Properties properties;

        if (args.length == 0) {
            println(header);
            String line;
            while (true){
                line = ScannerUtil.getLine(getCommandInput());
                if (line.equals("exit") || line.equals("quit")) die("bye", 0);
                else if (line.equals("help")) printLiveHelp();
                else if (line.equals("scp")) {
                    if (ScannerUtil.getVerboseInput("Insert arguments one by one? [Y,n]")){
                        properties = new Properties();
                        properties.put("user", ScannerUtil.getLine("Insert user:"));
                        properties.put("host", ScannerUtil.getLine("Insert host:"));
                        properties.put("port", ScannerUtil.getLine("Insert port:"));
                        properties.put("fileLocal", ScannerUtil.getPath("Insert local path to send:"));
                        properties.put("fileRemote", ScannerUtil.getPath("Insert remote path to set:"));
                    }
                    else {
                        args = ScannerUtil.getLineAsArray("Insert program arguments: ", " ");
                        properties = getPropertiesFromArgs(args);
                    }
                    if (properties != null) properties.put("Method", "scp");
                    else die("Null arguments", 0);
                    break;
                }else if (line.equals("sftp")) {
                    if (ScannerUtil.getVerboseInput("Insert arguments one by one? [Y,n]")){
                        properties = new Properties();
                        properties.put("user", ScannerUtil.getLine("Insert user:"));
                        properties.put("host", ScannerUtil.getLine("Insert host:"));
                        properties.put("port", ScannerUtil.getLine("Insert port:"));
                    }
                    else {
                        args = ScannerUtil.getLineAsArray("Insert program arguments: ", " ");
                        properties = getPropertiesFromArgs(args);
                    }
                    if (properties != null) properties.put("Method", "sftp");
                    else die("Null arguments", 0);
                    break;
                }
                else if (line.equals("shell") || line.equals("ssh")) {
                    properties = new Properties();
                    properties.put("user", ScannerUtil.getLine("Insert user:"));
                    properties.put("host", ScannerUtil.getLine("Insert host:"));
                    String port = ScannerUtil.getLine("Insert port[22]:");
                    if (port.isEmpty() || port.isBlank()) port = "22";
                    properties.put("port", port);
                    properties.put("Method", "shell");
                    properties.put("Debugging", "OFF");
                    break;
                }
                else if (line.equals("pssh")) {
                    properties = new Properties();
                    String[] confirm = {"Y","y","I","i", "F", "f", "file"};
                    String[] denied = {"N","n","M","m"};
                    if (ScannerUtil.getVerboseInput("Insert hosts file or Manual insertion? [I/m]",confirm,denied)) properties.put("fileLocal", ScannerUtil.getLine("File path: "));
                    else properties.put("Interactive", "1");
                    properties.put("Method", "pssh");
                    properties.put("Debugging", "OFF");
                    break;
                }else if (line.equals("encrypt")) {
                    properties = new Properties();
                    String[] confirm = {"l", "L"};
                    String[] denied = {"D", "d"};
                    if (ScannerUtil.getVerboseInput("Encrypt document or live encryption? [D/l]",confirm,denied)) properties.put("Interactive", "1");
                    else properties.put("fileLocal", ScannerUtil.getPath("File path: "));
                    properties.put("Method", "encrypt");
                    properties.put("Debugging", "OFF");
                    break;
                }else if (line.equals("decrypt")) {
                    properties = new Properties();
                    String[] confirm = {"l", "L"};
                    String[] denied = {"D", "d"};
                    if (ScannerUtil.getVerboseInput("Decrypt document or live encryption? [D/l]",confirm,denied)) properties.put("Interactive", "1");
                    else properties.put("fileLocal", ScannerUtil.getPath("File path: "));
                    properties.put("Method", "decrypt");
                    properties.put("Debugging", "OFF");
                    break;
                }
            }
        }else{
            properties = getPropertiesFromArgs(args);
        }

//            String[] required = {"scp", "sftp"};
//            if (ArgumentReaderUtil.isOneValid(properties, required)) ConsolePrinterUtil.die("Method required; use -scp, -sftp, -shell or -pssh on arguments to define one", 0);

        if (properties == null) {
            println("Transfer command not found");
            System.exit(0);
        }
        if (!properties.containsKey("Method")) die("method not defined", -1);

        if (properties.getProperty("Method").equals("scp")){
            SCPSender scpSender = new SCPSender(properties);
            scpSender.run();
            try{
                scpSender.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        else if (properties.getProperty("Method").equals("sftp")){
            SFTPSender sftpSender = new SFTPSender(properties);
            sftpSender.run();
            try{
                sftpSender.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }else if (properties.getProperty("Method").equals("shell")){
            SecureShell secureShell = new SecureShell(properties);
            secureShell.run();
            try{
                secureShell.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }else if (properties.getProperty("Method").equals("pssh")){
            ParallelSessionController parallelSessionController = new ParallelSessionController(properties);
            parallelSessionController.run();
            try{
                parallelSessionController.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }else if (properties.getProperty("Method").equals("encrypt")){
            String[] requiredProperties = {"fileLocal", "Interactive"};
            if (!isOneValid(properties, requiredProperties)) die(Main.class,"Missing required arguments", 0);
            encryptCall(properties);
        }else if (properties.getProperty("Method").equals("decrypt")){
            String[] requiredProperties = {"fileLocal", "Interactive"};
            if (!isOneValid(properties, requiredProperties)) die(Main.class,"Missing required arguments", 0);
            decryptCall(properties);
        }

    }

    private static Properties getPropertiesFromArgs(String[] args){
        try{
            return ArgumentReaderUtil.getParams(args);
        }catch (WrongArgumentException we){
            if(getPropertyOrDefault("Debugger", "ON").equals("OFF"))
                die(SCPSender.class, we.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
            else die("Error reading arguments, Enable Debugger on TransferTool.conf may help", -1);
        }
        return null;
    }

    private static void encryptCall(Properties properties){
        if (!EncryptionUtil.areKeysPresent()) EncryptionUtil.generateKey();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PUBLIC_KEY_PATH))) {
            RSAPublicKey publicKey = (RSAPublicKey) inputStream.readObject();
            if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
                while (true) {
                    String line = ScannerUtil.getLine("Text to encrypt (n to exit):");
                    if (line.equals("n")) die(0);
                    printByteEncrypted(Objects.requireNonNull(EncryptionUtil.encryptString(line, publicKey)));
                }
            } else {
                try {
                    CipherOutputStream out = new CipherOutputStream(new FileOutputStream(new File(properties.get("fileLocal") +".encrypted")), EncryptionUtil.getEncryptionCipher(publicKey));
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream((String)properties.get("fileLocal")));
                    int i;
                    while ((i = in.read()) != -1)
                        out.write(i);
                    out.flush();
                    die("File encrypted " + PathFinderUtil.getPathFileName(Path.of((String) properties.get("fileLocal"))), 0);
                } catch (IOException | TransferToolException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void decryptCall(Properties properties) {
        if (!EncryptionUtil.areKeysPresent()) EncryptionUtil.generateKey();

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH))) {
            RSAPrivateKey privateKey = (RSAPrivateKey) inputStream.readObject();
            if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
                while (true) {
                    String line = ScannerUtil.getLine("Text to decrypt (n to exit):");
                    if (line.equals("n")) die(0);
                    println(EncryptionUtil.decryptString(line, privateKey));
                }
            } else {
                try {
                    CipherInputStream in = new CipherInputStream(new FileInputStream((String) properties.get("fileLocal")), EncryptionUtil.getDecryptionCipher(privateKey));
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(properties.get("fileLocal") +".decrypted"));
                    int i;
                    while ((i = in.read()) != -1) bufferedOutputStream.write(i);
                    die("File decrypted " + PathFinderUtil.getPathFileName(Path.of((String) properties.get("fileLocal"))), 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
