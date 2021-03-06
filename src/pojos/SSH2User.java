package pojos;

import com.jcraft.jsch.*;
import exceptions.TransferToolException;
import utils.ConsolePrinterUtil;
import utils.ScannerUtil;
import java.util.Properties;
import static java.lang.Integer.*;
import static utils.ScannerUtil.*;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class SSH2User implements UserInfo, UIKeyboardInteractive {

    private boolean debugging;

    public SSH2User(boolean debugging){
        this.debugging = debugging;
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        try{
            return ScannerUtil.getPassword();
        }catch (TransferToolException e){
            return getLine("Error with password input; Enter Password as plain text: ");
        }
    }

    @Override
    public boolean promptPassphrase(String msg) {
        if (debugging) System.out.println("promptPassphrase : " + msg);
        return true;
    }

    @Override
    public boolean promptPassword(String msg) {
        if (debugging) System.out.println("promptPassword : " + msg);
        return true;
    }

    @Override
    public boolean promptYesNo(String msg) {
        return getVerboseInput(msg + " [Y/n]");
    }

    @Override
    public void showMessage(String msg) {
        System.out.println(msg);
    }

    /**
     * implements UIKeyboardInteractive to have access to this method.
     * we should implement the interaction.
     * @param destination string
     * @param name string
     * @param instruction string
     * @param prompt string][]
     * @param echo boolean[]
     * @return null
     */
    @Override
    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {

        if (debugging){
           System.out.println("promptKeyboardInteractive");
           System.out.println("destination: "+destination);
           System.out.println("name: "+name);
           System.out.println("instruction: "+instruction);
           System.out.println("prompt.length: "+prompt.length);
           System.out.println("prompt: "+prompt[0]);
        }

        return null ;
    }

    public static Session sshUser(String user, String port, String host, boolean debugging, Properties properties) throws JSchException {
        JSch jsch = new JSch();
//        try {
        Session session = jsch.getSession(user, host, parseInt(port));
        Properties strict = new Properties();
        if (properties != null && properties.contains("StrictHostKeyChecking")) if (properties.getProperty("StrictHostKeyChecking").equals("no")) strict.put("StrictHostKeyChecking", "no");
            else if (properties.getProperty("StrictHostKeyChecking").equals("yes")) strict.put("StrictHostKeyChecking", "yes");
            else if (debugging) ConsolePrinterUtil.printClassInfo(SSH2User.class, "StrictHostKeyChecking disabled");
        session.setConfig(strict);
        UserInfo ui = new SSH2User(debugging);
        session.setUserInfo(ui);
        return session;
//        }catch(IOException e){}
    }
}
