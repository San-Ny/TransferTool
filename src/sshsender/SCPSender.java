package sshsender;

import com.jcraft.jsch.*;
import pojos.SSH2User;
import utils.*;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
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
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));
            Properties strict = new Properties();
            strict.put("StrictHostKeyChecking", ConfigurationUtil.getPropertyOrDefault("StrictHostKeyChecking", "yes"));
            session.setConfig(strict);
            UserInfo ui = new SSH2User(debugging);
            session.setUserInfo(ui);

            try{
                session.connect();
            }catch(final JSchException jex){
                try{
                    if (ScannerUtil.getVerboseInput("Trust this host with fingerprint: " + session.getHostKey().getFingerPrint(jsch) + " [Y/n]: ")){
                        byte [] key = Base64.getDecoder().decode ( session.getHostKey().getKey());
                        HostKey hostKey = new HostKey(session.getHost(), key);
                        jsch.getHostKeyRepository().add (hostKey, null );
                    }
                }catch (NullPointerException nullPointer){
                    System.err.println("Host unreachable");
                    System.exit(0);
                }

                try{
                    session.connect(2000);
                }catch (JSchException end){
                    if (debugging) end.printStackTrace();
                    else System.err.println("There's no common cipher to choose from.\n\tIs Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files installed??");
                    System.exit(0);
                }

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