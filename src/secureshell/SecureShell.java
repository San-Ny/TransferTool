package secureshell;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import pojos.SSH2User;
import sshsender.SCPSendService;
import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.ConsolePrinterUtil;

import java.util.Properties;

public class SecureShell extends Thread {

    private Properties properties;

    public SecureShell(Properties properties){
       this.properties = properties;
    }

    @Override
    public void run() {
//        super.run();
        String[] requiredProperties = {"user", "host"};

        if (!ArgumentReaderUtil.isValid(properties, requiredProperties)) ConsolePrinterUtil.die(SCPSender.class,"Missing required arguments", 0);

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
                if (debugging) ConsolePrinterUtil.die(SCPSender.class, jex.getMessage(),-1, Thread.currentThread().getStackTrace()[1].getLineNumber());
                else ConsolePrinterUtil.die(SCPSender.class,"Connection failed", -1);
            }

            Channel channel = session.openChannel("shell");

            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect();

            while (true) if (channel.isClosed()) {
                session.disconnect();
                break;
            }


        }catch (JSchException e){
            if (debugging)ConsolePrinterUtil.printDebugging(SCPSender.class, e.getMessage(), Thread.currentThread().getStackTrace()[1].getLineNumber());
            else ConsolePrinterUtil.die(SCPSender.class,"Transfer failed", 0);
        }
    }
}
