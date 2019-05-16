package sftpsender;

import utils.ArgumentReaderUtil;

import java.util.Properties;

/**
 * TransferTool
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

        if (!ArgumentReaderUtil.isValid(properties, requiredProperties)){
            System.err.println("Missing required arguments");
            System.exit(0);
        }
    }
}
