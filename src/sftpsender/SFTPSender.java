package sftpsender;

import java.util.Properties;
import static utils.ArgumentReaderUtil.*;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class SFTPSender extends Thread {

    private Properties properties;

    public SFTPSender(Properties properties){
        this.properties = properties;
    }

    @Override
    public void run(){

        //checking required arguments
        String[] requiredProperties = {"user", "port", "host"};

        if (isNotValid(properties, requiredProperties)){
            System.err.println("Missing required arguments");
            System.exit(0);
        }

        if (properties.containsKey("Upload") || properties.containsKey("Download")){
            String[] uploadRequired = {"localFile", "remoteFile"};

            if (isNotValid(properties, uploadRequired)){
                System.err.println("Missing required arguments");
                System.exit(0);
            }

            if (properties.containsKey("Upload")){
                SFTPUpload sftpUpload = new SFTPUpload(properties);
                sftpUpload.run();
                try {
                    sftpUpload.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }else{
                SFTPGet sftpGet = new SFTPGet(properties);
                sftpGet.run();
                try {
                    sftpGet.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }else{
            SFTPlive sftPlive = new SFTPlive(properties);
            sftPlive.run();
            try {
                sftPlive.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
