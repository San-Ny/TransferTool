package main;

import exceptions.WrongArgumentException;
import sftpsender.SFTPSender;
import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.ConfigurationUtil;

import java.io.IOException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties properties = null;

        try{
            properties = ArgumentReaderUtil.getParams(args);
        }catch (WrongArgumentException we){
            if(ConfigurationUtil.getPropertyOrDefault("Debugger", "ON").equals("OFF")) we.printStackTrace();
            else System.err.println("Error reading arguments, Enable Debugger on TransferTool.conf");
            System.exit(0);
        }

        String[] required = {"ssh", "sftp"};
        if (ArgumentReaderUtil.isOneValid(properties, required)){
            System.err.println("Method required; use -ssh or -sftp on arguments to define one");
        }

        if (properties.getProperty("Method").equals("ssh")){
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
        }

    }
}
