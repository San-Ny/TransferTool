package parallelshell;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import pojos.Host;
import pojos.SSH2User;
import sshsender.SCPSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import static utils.ConsolePrinterUtil.die;
import static utils.ConsolePrinterUtil.printDebugging;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class ActiveShell{

    private boolean debugging;
    private int position;
    private Channel channel;
    private Session session;

    public ActiveShell(Host host){
        Properties properties = host.getProperties();
        this.position = host.getPosition();
        if (properties != null && properties.containsKey("Debugging") && properties.getProperty("Debugging").equals("ON")) debugging = true;

        try {
            session = SSH2User.sshUser(host.getUser(), host.getPort(), host.getHost(), false, properties);
            try{
                session.connect();
            }catch(final JSchException jex){
                if (debugging) die(SCPSender.class, jex.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
                else die(SCPSender.class,"Connection failed", -1);
            }

            channel = session.openChannel("exec");
        }catch (JSchException je){
            if (debugging) printDebugging(SCPSender.class, je.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else die(SCPSender.class,"Unable to open exec channel", 0);
        }
    }

    public void exec(String command) {

          ((ChannelExec)channel).setCommand(command);

          channel.setInputStream(null);
//                channel.setOutputStream(System.out);
          ((ChannelExec)channel).setErrStream(System.err);

        try {
//            InputStream in = channel.getInputStream();
//            OutputStream out = channel.getOutputStream();
            channel.connect();
        } catch (JSchException e) {
//        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        channel.disconnect();
        session.disconnect();
    }
}
