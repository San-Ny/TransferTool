package secureshell;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import pojos.SSH2User;
import sshsender.SCPSender;

import java.util.Properties;

import static utils.ArgumentReaderUtil.*;
import static utils.ConsolePrinterUtil.*;

public class SecureShell extends Thread {

    private Properties properties;

    public SecureShell(Properties properties){
       this.properties = properties;
    }

    @Override
    public void run() {
//        super.run();
        String[] requiredProperties = {"user", "host"};

        if (isNotValid(properties, requiredProperties)) die(SCPSender.class,"Missing required arguments", 0);

        //assignation
        String user, port, host;
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");


        try{
            Session session = SSH2User.sshUser(user, port, host, debugging, properties);

            try{
                session.connect();
            }catch(final JSchException jex){
                if (debugging) die(SCPSender.class, jex.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
                else die(SCPSender.class,"Connection failed", -1);
            }

            Channel channel = session.openChannel("shell");

            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect();

            while (true) if (channel.isClosed()) {
                session.disconnect();
                System.exit(0);
            }


        }catch (JSchException e){
            if (debugging) printDebugging(SCPSender.class, e.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else die(SCPSender.class,"Transfer failed", 0);
        }
    }
}
