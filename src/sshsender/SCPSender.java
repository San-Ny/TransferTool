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
            System.err.println("Missing required arguments");
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
            if (debugging) pe.printStackTrace();
            else System.err.println("Error on local path");
            System.exit(0);
        }

        try{
            Session session = SSH2User.sshUser(user, port, host, debugging, properties);

            try{
                session.connect();
            }catch(final JSchException jex){
                if (debugging) jex.printStackTrace();
                else System.err.println("Incomplete connection");
                System.exit(-1);
            }

            SCPSendService[] fileToScp = new SCPSendService[paths.size()];
            for (int a = 0; a < fileToScp.length; a++){
                if (debugging) System.out.println("New Thread: mission scp -> " + paths.get(a) + " to " + properties.getProperty("fileRemote"));
                fileToScp[a] = new SCPSendService(session, properties, paths.get(a),debugging, PathFinderUtil.getPathFileName(paths.get(a)));
                fileToScp[a].run();
                if (debugging) System.out.println("Thread running");
            }

            try{
                for (SCPSendService SCPSendService : fileToScp) SCPSendService.join();
            }catch (InterruptedException in){
                if (debugging) in.printStackTrace();
                else System.err.println("Share subprocess interrupted, data will be corrupt or incomplete!");
                System.exit(-1);
            }

            if (fileLocal.length() != 1) System.out.println("All files transferred");
            else System.out.println("File transferred");
            session.disconnect();
            System.exit(0);

        }catch (JSchException e){
            if (debugging)e.printStackTrace();
            else System.err.println("Transfer failed");
        }
    }
}