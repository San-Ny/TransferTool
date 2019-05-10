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
    public static void main(String[] args) {

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
        String user, port, host, fileLocal, fileRemote;
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");
        fileLocal = properties.getProperty("fileLocal");
        fileRemote = properties.getProperty("fileRemote");


        //getting paths array; cleaning it
        try{
            ArrayList<Path> paths = PathFinderUtil.getCorrectFormat(Path.of(fileLocal), properties);
        }catch (IOException pe){
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) pe.printStackTrace();
            else System.err.println("Error on local path");
        }

        try{
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));

//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", ConfigurationUtil.getPropertyOrDefault("StrictHostKeyChecking", "yes"));
//            config.put("cipher.s2c", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
//            config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
//            session.setConfig("kex", "diffie-hellman-group1-sha1");
//            session.setConfig(config);
//            session.setPassword(ScannerUtil.getPassword());

//            session.setPort(Integer.parseInt(properties.getProperty("port")));
//            session.setHost(properties.getProperty("host"));
//            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");

            UserInfo ui=new SSH2User();
            session.setUserInfo(ui);

            try{
                session.connect();
            }catch(final JSchException jex){

                if (ScannerUtil.getVerboseInput("Trust this host with fingerprint: " + session.getHostKey().getFingerPrint(jsch) + " [Y/n]:")){
                    byte [] key = Base64.getDecoder().decode ( session.getHostKey().getKey());
                    HostKey hostKey = new HostKey(session.getHost(), key);
                    jsch.getHostKeyRepository().add (hostKey, null );
                }
                try{
                    session.connect(2000);
                }catch (JSchException end){
                    if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) end.printStackTrace();
                    else System.err.println("There's no common cipher to choose from.\n\tIs Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files installed??");
                    System.exit(0);
                }

            }

            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1") || properties.getProperty("Debugging").equals("1")) System.out.println("Connected to remote host, sharing files;");

            fileRemote = fileRemote.replace("'", "'\"'\"'");
            fileRemote = "'" + fileRemote + "'";
            String command = "scp " + "-p" + " -t " + fileRemote;
            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            OutputStream out = null;
            InputStream in = null;
            try{
                out = channel.getOutputStream();
                in = channel.getInputStream();
            }catch (IOException ioe){
                if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1")|| properties.getProperty("Debugging").equals("1"))ioe.printStackTrace();
                else System.err.println("Broken Streams");
                System.exit(0);
            }

            channel.connect();

            //checking stream status, if not valid exception will be thrown
            bufferStatus(in);

            //sending files



        }catch (JSchException | IOException | TransferToolException e){
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1")|| properties.getProperty("Debugging").equals("1"))e.printStackTrace();
            else System.err.println("Transfer failed");
        }


    }

    static void bufferStatus(InputStream in) throws TransferToolException, IOException{
        int b=in.read();
        if(b != 0) {
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = in.read()) != -1) sb.append((char) i);
            throw new TransferToolException("Error on stream" + sb.toString());
        }
    }
}