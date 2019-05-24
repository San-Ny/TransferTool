package cluster;

import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.ConsolePrinterUtil;
import utils.ScannerUtil;

import java.util.HashMap;
import java.util.Properties;

import static utils.ConsolePrinterUtil.die;

/**
 * NOTICE ALL HOSTS MUST KNOW THE MAIN HOST KEY or password will be asked
 */
public class ParallelSessionController extends Thread{

    private Properties properties;

    public ParallelSessionController(Properties properties){
        this.properties = properties;
    }

    @Override
    public void run() {
        super.run();

        //checking required arguments
        String[] requiredProperties = {"fileLocal"};
        if (ArgumentReaderUtil.isNotValid(properties, requiredProperties)) die(SCPSender.class,"Missing required arguments", 0);


        //assignation
        HashMap<String, String> hosts;
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        if (properties.contains("Interactive") && properties.getProperty("Interactive").equals("1")) {
            //inserting hosts
            hosts = new HashMap<>();
            while (true){
                String option = ScannerUtil.getLine("1 Insert Host\n2 Remove host\n3 Finish\n4 Cancel");
                if (option.equals("1")){
                    hosts.put(ScannerUtil.getLine("User:"), ScannerUtil.getLine("Host:"));
                    continue;
                }else if (option.equals("2")){
                    hosts.remove(ScannerUtil.getLine("User:"), ScannerUtil.getLine("Host:"));
                    continue;
                }else if (option.equals("3")) break;
                else if (option.equals("4")) ConsolePrinterUtil.die("bye", 0);
            }
        }

    }
}
