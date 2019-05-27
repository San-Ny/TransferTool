package main;

import parallelshell.ParallelSessionController;
import exceptions.WrongArgumentException;
import secureshell.SecureShell;
import sftpsender.SFTPSender;
import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.ConfigurationUtil;
import utils.ConsolePrinterUtil;
import utils.ScannerUtil;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        Properties properties = new Properties();

        if (args.length == 0) {
            ConsolePrinterUtil.println(ConsolePrinterUtil.header);
            String line;
            while (true){
                line = ScannerUtil.getLine(ConsolePrinterUtil.getCommandInput());
                if (line.equals("exit") || line.equals("quit")) ConsolePrinterUtil.die("bye", 0);
                else if (line.equals("help")) ConsolePrinterUtil.printLiveHelp();
                else if (line.equals("scp")) {
                    args = ScannerUtil.getLineAsArray("Insert program arguments: ", " ");
                    properties = getPropertiesFromArgs(args);
                    if (properties != null) properties.put("Method", "scp");
                    else ConsolePrinterUtil.die("Null arguments", 0);
                    break;
                }else if (line.equals("sftp")) {
                    args = ScannerUtil.getLineAsArray("Insert program arguments: ", " ");
                    properties = getPropertiesFromArgs(args);
                    if (properties != null) properties.put("Method", "sftp");
                    else ConsolePrinterUtil.die("Null arguments", 0);
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
                    if (ScannerUtil.getVerboseInput("Insert hosts file? No for manual insertion. [Y/n]")) properties.put("fileLocal", ScannerUtil.getLine("File path: "));
                    else properties.put("Interactive", "1");
                    properties.put("Method", "pssh");
                    properties.put("Debugging", "OFF");
                    break;
                }
            }
        }else{
            properties = getPropertiesFromArgs(args);
        }

//            String[] required = {"scp", "sftp"};
//            if (ArgumentReaderUtil.isOneValid(properties, required)) ConsolePrinterUtil.die("Method required; use -scp, -sftp, -shell or -parallelshell on arguments to define one", 0);

        if (properties == null) {
            ConsolePrinterUtil.println("Transfer command not found");
            System.exit(0);
        }
        if (!properties.containsKey("Method")) ConsolePrinterUtil.die("method not defined", -1);

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
        }

    }

    private static Properties getPropertiesFromArgs(String[] args){
        try{
            return ArgumentReaderUtil.getParams(args);
        }catch (WrongArgumentException we){
            if(ConfigurationUtil.getPropertyOrDefault("Debugger", "ON").equals("OFF"))
                ConsolePrinterUtil.die(SCPSender.class, we.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
            else ConsolePrinterUtil.die("Error reading arguments, Enable Debugger on TransferTool.conf", -1);
        }
        return null;
    }
}
