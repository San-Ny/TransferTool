package sshsender;

import com.jcraft.jsch.*;
import pojos.SSH2User;
import utils.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class SCPSender extends Thread {

    private Properties properties;

    public SCPSender(Properties properties){
        this.properties = properties;
    }

    @Override
    public void run() {

        //checking required arguments
        String[] requiredProperties = {"user", "port", "host", "fileLocal", "fileRemote"};

        if (!ArgumentReaderUtil.isValid(properties, requiredProperties)){
            ConsolePrinterUtil.printClassInfo(SCPSender.class,"Missing required arguments");
            System.exit(0);
        }


        //assignation
        String user, port, host, fileLocal;
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");
        fileLocal = properties.getProperty("fileLocal");


        //getting paths array; cleaning it
        ArrayList<Path> paths = null;
        try{
            paths = PathFinderUtil.getCorrectFormat(Path.of(fileLocal), properties);
        }catch (IOException pe){
            if (debugging) ConsolePrinterUtil.printDebugging(SCPSender.class, pe.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else ConsolePrinterUtil.printClassInfo(SCPSender.class,"Error on local path");
            System.exit(0);
        }

        try{
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));
            Properties strict = new Properties();
            if (properties.getProperty("StrictHostKeyChecking").equals("no")) strict.put("StrictHostKeyChecking", "no");
            else if (properties.getProperty("StrictHostKeyChecking").equals("yes")) strict.put("StrictHostKeyChecking", "yes");
            else if (debugging) ConsolePrinterUtil.printDebugging(SCPSender.class, "StrictHostKeyChecking disabled", Thread.currentThread().getStackTrace()[1].getLineNumber());
            session.setConfig(strict);
            UserInfo ui = new SSH2User(debugging);
            session.setUserInfo(ui);

            try{
                session.connect();
            }catch(final JSchException jex){
                if (debugging) ConsolePrinterUtil.printDebugging(SCPSender.class, jex.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
                else ConsolePrinterUtil.printClassInfo(SCPSender.class,"Incomplete connection");
                System.exit(-1);
            }

            SCPSendService[] fileToScp = new SCPSendService[paths.size()];
            for (int a = 0; a < fileToScp.length; a++){
                if (debugging) ConsolePrinterUtil.printDebugging(SCPSender.class,"New Thread: mission scp -> " + paths.get(a) + " to " + properties.getProperty("fileRemote"), Thread.currentThread().getStackTrace()[1].getLineNumber());
                fileToScp[a] = new SCPSendService(session, properties, paths.get(a),debugging, PathFinderUtil.getPathFileName(paths.get(a)));
                fileToScp[a].run();
                if (debugging) ConsolePrinterUtil.printClassInfo(SCPSender.class,"Thread running");
            }

            try{
                for (SCPSendService SCPSendService : fileToScp) SCPSendService.join();
            }catch (InterruptedException in){
                if (debugging) ConsolePrinterUtil.printDebugging(SCPSender.class, in.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
                else ConsolePrinterUtil.printClassInfo(SCPSender.class,"Share subprocess interrupted, data will be corrupt or incomplete!");
                System.exit(-1);
            }

            if (fileLocal.length() != 1) ConsolePrinterUtil.printClassInfo(SCPSender.class,"All files transferred");
            else ConsolePrinterUtil.printClassInfo(SCPSender.class,"File transferred");
            session.disconnect();
            System.exit(0);

        }catch (JSchException e){
            if (debugging)ConsolePrinterUtil.printDebugging(SCPSender.class, e.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else ConsolePrinterUtil.printClassInfo(SCPSender.class, "Transfer failed");
        }
    }
}