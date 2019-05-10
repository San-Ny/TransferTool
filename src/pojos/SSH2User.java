package pojos;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import utils.ScannerUtil;

import java.io.IOException;

public class SSH2User implements UserInfo, UIKeyboardInteractive {

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        try{
            return ScannerUtil.getPassword();
        }catch (IOException e){
            System.err.println("Unable to get console instance");
            System.exit(-1);
        }
        return null;
    }

    @Override
    public boolean promptPassphrase(String msg) {
        System.out.println("promptPassphrase : "+msg);
        return true;
    }

    @Override
    public boolean promptPassword(String msg) {
        System.out.println("promptPassword : "+msg);
        return true;
    }

    @Override
    public boolean promptYesNo(String msg) {
        return ScannerUtil.getVerboseInput(msg + " [Y/n]");
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

        System.out.println("promptKeyboardInteractive");
        System.out.println("destination: "+destination);
        System.out.println("name: "+name);
        System.out.println("instruction: "+instruction);
        System.out.println("prompt.length: "+prompt.length);
        System.out.println("prompt: "+prompt[0]);

        return null ;
    }
}
