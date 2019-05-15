package sender;

import com.jcraft.jsch.*;
import exceptions.TransferToolException;
import exceptions.WrongArgumentException;
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

public class Sender {

    /**
     * Core program, creates the ssh session according to arguments and sends the requested files to the selected path
     *
     * @param args program arguments like file paths, ser, host, port, etc
     */
    public static void main(String[] args) throws TransferToolException {

        //check conf file
        if (!ConfigurationUtil.isConfigPresent())ConfigurationUtil.generateConf();
        Properties properties = null;

        try{
            properties = ArgumentReaderUtil.getParams(args);
        }catch (WrongArgumentException we){
            we.printStackTrace();
            System.exit(0);
        }

        //checking arguments
        String[] requiredProperties = {"user", "port", "host", "fileLocal", "fileRemote"};
        for(String s : requiredProperties){
            if (!properties.containsKey(s)) {
                System.err.println("Missing required arguments");
                System.exit(0);
            }
        }

        //assignation
        String user, port, host, fileLocal;
        boolean debugging = false;
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");
        fileLocal = properties.getProperty("fileLocal");
        if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) debugging = true;


        //getting paths array; cleaning it
        ArrayList<Path> paths = null;
        try{
            paths = PathFinderUtil.getCorrectFormat(Path.of(fileLocal), properties);
        }catch (IOException pe){
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) pe.printStackTrace();
            else System.err.println("Error on local path");
            System.exit(0);
        }

        try{
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));

            UserInfo ui = new SSH2User();
            session.setUserInfo(ui);

            try{
                session.connect();
            }catch(final JSchException jex){
                try{
                    if (ScannerUtil.getVerboseInput("Trust this host with fingerprint: " + session.getHostKey().getFingerPrint(jsch) + " [Y/n]:")){
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

            SendService[] fileToScp = new SendService[paths.size()];
            for (int a = 0; a < fileToScp.length; a++){
                if (debugging) System.out.println("New Thread: mission scp -> " + paths.get(a) + "to " + properties.getProperty("fileRemote"));
                fileToScp[a] = new SendService(session, properties, paths.get(a),debugging, PathFinderUtil.getPathFileName(paths.get(a)));
                fileToScp[a].run();
                if (debugging) System.out.println("Thread running");
            }

            try{
                for (SendService sendService : fileToScp) sendService.join();
            }catch (InterruptedException in){
                if (debugging) throw new TransferToolException(in.getMessage());
                else System.err.println("Share subprocess interrupted, data will be corrupt or incomplete!");
                System.exit(-1);
            }

            if (fileLocal.length() != 1) System.out.println("All files transferred");
            else System.out.println("File transferred");
            session.disconnect();
            System.exit(0);

        }catch (JSchException e){
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1")|| properties.getProperty("Debugging").equals("1"))e.printStackTrace();
            else System.err.println("Transfer failed");
        }
    }
}