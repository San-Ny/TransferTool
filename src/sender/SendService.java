package sender;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import exceptions.TransferToolException;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class SendService extends Thread {

    private Session session;
    private Properties properties;
    private Path path;
    private boolean debugging;

    public SendService(Session session, Properties properties, Path path, boolean debugging) {
        this.session = session;
        this.properties = properties;
        this.path = path;
        this.debugging = debugging;
    }

    @Override
    public void run() {
        super.run();

        BufferedInputStream fis = null;
        try{
            if (debugging) System.out.println("Connected!");

            String fileRemote = properties.getProperty("fileRemote");

            fileRemote = fileRemote.replace("'", "'\"'\"'");
            fileRemote = "'" + fileRemote + "'";
            String command = "scp " + "-p" + " -t " + fileRemote;

            Channel channel = session.openChannel("exec");
            ((ChannelExec)channel).setCommand(command);

            BufferedReader in = null;
            BufferedWriter out = null;
            try{
                out = new BufferedWriter(new OutputStreamWriter(channel.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
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
            out.write(command);
            out.flush();

            bufferStatus(in);

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize=file.length();
            command="C0644 "+filesize+" ";

            if(fileRemote.lastIndexOf('/')>0) command+=fileRemote.substring(fileRemote.lastIndexOf('/')+1);

            else command += fileRemote;
            command+="\n";
            out.write(command);
            out.flush();

            bufferStatus(in);

            // send a content of lfile
            fis=new BufferedInputStream(fileRemote);
            byte[] buf=new byte[1024];
            while(true){
                int len = fis.read(buf, 0, buf.length);
                if(len <= 0) break;
                out.write(buf); //out.flush();
            }
            fis.close();

            // send '\0'
            buf[0]=0; out.write("0"); out.flush();

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

    static void bufferStatus(BufferedReader in) throws TransferToolException, IOException{
        int b=in.read();
        if(b != 0) {
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i = in.read()) != -1) sb.append((char) i);
            throw new TransferToolException("Error on stream" + sb.toString());
        }
    }
}
