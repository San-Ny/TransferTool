package sender;

import com.jcraft.jsch.*;
import exceptions.TransferToolException;
import exceptions.WrongArgumentException;
import utils.*;

import java.io.*;
import java.nio.file.Path;
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
            if (PathFinderUtil.hasAsterisk(fileLocal)) PathFinderUtil.getAllRecursivePaths(Path.of(PathFinderUtil.removeAsterisk(fileLocal)));
            else if (properties.containsKey("verbose")) PathFinderUtil.getVerbosePaths(Path.of(fileLocal));
        }catch (IOException pe){
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1")) pe.printStackTrace();
            else System.err.println("Error on local path");
        }

        try{
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));
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
                if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1"))ioe.printStackTrace();
                else System.err.println("Broken Streams");
                System.exit(0);
            }

            channel.connect();

            //checking stream status, if not valid exception will be thrown
            bufferStatus(in);

            //sending files



        }catch (JSchException | IOException | TransferToolException e){
            if (ConfigurationUtil.getPropertyOrDefault("Debugging", "0").equals("1"))e.printStackTrace();
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