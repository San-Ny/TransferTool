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

        if (!ArgumentReaderUtil.isValid(properties, requiredProperties)) ConsolePrinterUtil.die(SCPSender.class,"Missing required arguments", 0);


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
            if (debugging) ConsolePrinterUtil.die(SCPSender.class, pe.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
            else ConsolePrinterUtil.die(SCPSender.class,"Error on local path", 0);
        }

        try{
            Session session = SSH2User.sshUser(user, port, host, debugging, properties);

            try{
                session.connect();
            }catch(final JSchException jex){
                if (debugging) ConsolePrinterUtil.die(SCPSender.class, jex.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
                else ConsolePrinterUtil.die(SCPSender.class,"Connection failed", -1);
            }

            SCPSendService[] fileToScp = new SCPSendService[0];
            if (paths != null) fileToScp = new SCPSendService[paths.size()];
            else ConsolePrinterUtil.die(SCPSender.class,"Null path" ,0);
            for (int a = 0; a < fileToScp.length; a++){
                if (debugging)ConsolePrinterUtil.printDebugging(SCPSender.class, "New Thread: mission scp -> " + paths.get(a) + " to " + properties.getProperty("fileRemote"), Thread.currentThread().getStackTrace()[1].getLineNumber());
                fileToScp[a] = new SCPSendService(session, properties, paths.get(a),debugging, PathFinderUtil.getPathFileName(paths.get(a)));
                fileToScp[a].run();
                if (debugging) ConsolePrinterUtil.printClassInfo(SCPSender.class,"Thread running");
            }

            try{
                for (SCPSendService SCPSendService : fileToScp) SCPSendService.join();
            }catch (InterruptedException in){
                if (debugging) ConsolePrinterUtil.die(SCPSender.class, in.getMessage(), -1, Thread.currentThread().getStackTrace()[1].getLineNumber());
                else ConsolePrinterUtil.die(SCPSender.class, "Share subprocess interrupted, data will be corrupt or incomplete!", -1);
            }

            if (fileLocal.length() != 1) ConsolePrinterUtil.die(SCPSender.class, session, "All files transferred", 0);
            else ConsolePrinterUtil.die(SCPSender.class, "File transferred", 0);

        }catch (JSchException e){
            if (debugging)ConsolePrinterUtil.printDebugging(SCPSender.class, e.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else ConsolePrinterUtil.die(SCPSender.class,"Transfer failed", 0);
        }
    }
}