package main;

import parallelshell.ParallelSessionController;
import exceptions.WrongArgumentException;
import secureshell.SecureShell;
import sftpsender.SFTPSender;
import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.ScannerUtil;
import java.util.Properties;
import static utils.ArgumentReaderUtil.isOneValid;
import static utils.ConfigurationUtil.*;
import static utils.ConsolePrinterUtil.*;
import static utils.EncryptionUtil.*;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class Main {
    public static void main(String[] args) {

        Properties properties;

        if (args.length == 0 || args.length == 1 && args[0].equals("nogui")) {
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
        }else if (properties.getProperty("Method").equals("pssh")) {
            ParallelSessionController parallelSessionController = new ParallelSessionController(properties);
            parallelSessionController.run();
            try {
                parallelSessionController.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if (properties.getProperty("Method").equals("encrypt")){
            String[] requiredProperties = {"fileLocal", "Interactive"};
            if (!isOneValid(properties, requiredProperties)) die(Main.class,"Missing required arguments", 0);
            encryptFile(properties);
        }else if (properties.getProperty("Method").equals("decrypt")){
            String[] requiredProperties = {"fileLocal", "Interactive"};
            if (!isOneValid(properties, requiredProperties)) die(Main.class,"Missing required arguments", 0);
            decryptFile(properties);
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
}
