package sender;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import exceptions.TransferToolException;
import utils.PathFinderUtil;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class SendService extends Thread {

    private Session session;
    private Properties properties;
    private Path path;
    private boolean debugging;
    private String newFileName;

    public SendService(Session session, Properties properties, Path path, boolean debugging, String newFileName) {
        this.session = session;
        this.properties = properties;
        this.path = path;
        this.debugging = debugging;
        this.newFileName = newFileName;
    }

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

            if (debugging) System.out.println("Channel created on session!");

            BufferedInputStream in = null;
            BufferedOutputStream out = null;
            try{
                out = new BufferedOutputStream(channel.getOutputStream());
                in = new BufferedInputStream(channel.getInputStream());
            }catch (IOException ioe){
                if (debugging)ioe.printStackTrace();
                else System.err.println("Broken Streams");
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
            if (debugging) System.out.println("File transferred");
        }catch (JSchException | IOException | TransferToolException e){
            if (debugging) e.printStackTrace();
            else System.err.println("Error sending file");
            System.exit(-1);
        }
    }

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
