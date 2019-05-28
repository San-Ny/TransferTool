package sshsender;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import exceptions.TransferToolException;
import utils.ConsolePrinterUtil;
import utils.PathFinderUtil;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

import static utils.ConsolePrinterUtil.*;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class SCPSendService extends Thread {

    private Session session;
    private Properties properties;
    private Path path;
    private boolean debugging;
    private String newFileName;

    /**
     * constructor to instance variables
     * @param session ssh session
     * @param properties user properties
     * @param path path to local file
     * @param debugging if debugging messages will be shown
     * @param newFileName a new file name if requested
     */
    public SCPSendService(Session session, Properties properties, Path path, boolean debugging, String newFileName) {
        this.session = session;
        this.properties = properties;
        this.path = path;
        this.debugging = debugging;
        this.newFileName = newFileName;
    }

    /**
     * creates an exec channel on the session to execute commands and write the data
     */
    @Override
    public void run() {
        super.run();

        BufferedInputStream fis;
        try{
            String fileRemote = properties.getProperty("fileRemote");
            if (PathFinderUtil.hasFinalBar(properties.getProperty("fileRemote"))) fileRemote = properties.getProperty("fileRemote") + newFileName;

            fileRemote = fileRemote.replace("'", "'\"'\"'");
            fileRemote = "'" + fileRemote + "'";
            String command = "scp " + "-p" + " -t " + fileRemote;

            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            if (debugging) printClassInfo(SCPSendService.class,"Channel created on session!");

            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            try{
                out = new BufferedOutputStream(channel.getOutputStream());
                in = new BufferedInputStream(channel.getInputStream());
            }catch (IOException ioe){
                if (debugging) printDebugging(SCPSendService.class, ioe.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
                else printClassInfo(SCPSendService.class,"Broken Streams");
                System.exit(0);
            }

            channel.connect();

            //checking stream status, if not valid exception will be thrown
            bufferStatus(in);

            File file = new File(path.toString());

            command = "T" + (file.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
            command += (" " + (file.lastModified() / 1000) + " 0\n");
            out.write(command.getBytes());
            out.flush();

            bufferStatus(in);

            // send "C0644 l filename", where filename should not include '/'
            command = "C0644 " + file.length() + " ";

            if(fileRemote.lastIndexOf('/') > 0) command += fileRemote.substring(fileRemote.lastIndexOf('/') + 1);
            else command += fileRemote;

            command += "\n";
            out.write(command.getBytes());
            out.flush();

            bufferStatus(in);

            // send a content of local file
            fis = new BufferedInputStream(new FileInputStream(path.toString()));

            int content;
            while ((content = fis.read()) != -1) {
                // convert to char and display it
                out.write((char) content);
            }
            fis.close();

            // send '\0'
            out.write(0);
            out.flush();

            bufferStatus(in);

            out.close();
            channel.disconnect();
            if (debugging) printDebugging(SCPSendService.class, "File transferred", Thread.currentThread().getStackTrace()[1].getLineNumber());
        }catch (JSchException | IOException | TransferToolException e){
            if (debugging) printDebugging(SCPSendService.class, e.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else printClassInfo(SCPSendService.class,"Error sending file");
            System.exit(-1);
        }
    }

    /**
     * checks buffer status; if closed will throw Exception
     * @param in BufferedInputStream to read data
     * @throws TransferToolException If the function is able to read the data
     * @throws IOException read method unsuccessful
     */
    static void bufferStatus(BufferedInputStream in) throws TransferToolException, IOException{
        int b=in.read();
        if(b != 0) {
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = in.read()) != -1) sb.append((char) i);
            throw new TransferToolException("Error on stream" + sb.toString());
        }
    }
}
