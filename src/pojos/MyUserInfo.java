package pojos;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import utils.ScannerUtil;

import java.io.IOException;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

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
    public boolean promptPassphrase(String arg0) {
        return true;
    }

    @Override
    public boolean promptPassword(String arg0) {
        return true;
    }

    @Override
    public boolean promptYesNo(String arg0) {
        return ScannerUtil.getVerboseInput(arg0 + " [Y/n]");
    }

    @Override
    public void showMessage(String arg0) {
        System.out.println(arg0);
    }

    @Override
    public String[] promptKeyboardInteractive(String arg0, String arg1, String arg2, String[] arg3, boolean[] arg4) {
        return null;
    }
}
