package sftpsender;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import pojos.MyProgressMonitor;
import pojos.SSH2User;

import java.util.Properties;

public class SFTPGet extends Thread {

    private String user, port, host;
    boolean debugging;
    Properties properties;

    public SFTPGet(Properties properties){
        debugging = properties.getProperty("Debugging").equals("ON");
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");
        this.properties = properties;
    }

    @Override
    public void run() {
        super.run();
        try{
            Session session = SSH2User.sshUser(user, port, host, debugging, properties);
            Channel channel;
            ChannelSftp sftp;

            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp)channel;

            MyProgressMonitor monitor = new MyProgressMonitor();
            int mode = ChannelSftp.OVERWRITE;
            sftp.get(properties.getProperty("fileRemote"), properties.getProperty("fileLocal"), monitor, mode);

            session.disconnect();
        } catch (Exception e){
            if (debugging)e.printStackTrace();
            else System.err.println("Fatal Error");
            System.exit(-1);
        }
    }
}
