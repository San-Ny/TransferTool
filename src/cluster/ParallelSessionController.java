package cluster;

import sshsender.SCPSender;
import utils.ArgumentReaderUtil;
import utils.ConsolePrinterUtil;
import utils.ScannerUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
//        String[] requiredProperties = {"fileLocal"};
//        if (ArgumentReaderUtil.isNotValid(properties, requiredProperties)) die(SCPSender.class,"Missing required arguments", 0);


        //assignation
        HashMap<String, String> hosts;
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
            //inserting hosts
            hosts = new HashMap<>();
            while (true){
                String option = ScannerUtil.getLine("  1 Insert Host\n  2 Remove host\n  3 list hosts\n  4 Finish\n  5 Cancel\n" + ConsolePrinterUtil.getCommandInput());
                if (option.equals("1")){
                    hosts.put(ScannerUtil.getLine("User:"), ScannerUtil.getLine("Host:"));
                }else if (option.equals("2")){
                    hosts.remove(ScannerUtil.getLine("User:"), ScannerUtil.getLine("Host:"));
                }else if (option.equals("3")) hosts.forEach((v, k) -> System.out.println("\t\tU:" + v + "\t\tH:" + k));
                else if (option.equals("4")) break;
                else if (option.equals("5")) ConsolePrinterUtil.die("bye", 0);
            }
        }

    }
}
