package parallelshell;

import java.util.HashMap;
import java.util.Properties;

import static utils.ConsolePrinterUtil.*;
import static utils.ScannerUtil.*;

/**
 * NOTICE ALL HOSTS MUST KNOW THE MAIN HOST KEY or password will be asked
 */
/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
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
                String option = getLine("  1 Insert Host\n  2 Remove host\n  3 list hosts\n  4 Finish\n  5 Cancel\n" + getCommandInput());
                if (option.equals("1")){
                    hosts.put(getLine("User:"), getLine("Host:"));
                }else if (option.equals("2")){
                    hosts.remove(getLine("User:"), getLine("Host:"));
                }else if (option.equals("3")) hosts.forEach((v, k) -> System.out.println("\t\tU:" + v + "\t\tH:" + k));
                else if (option.equals("4")) break;
                else if (option.equals("5")) die("bye", 0);
            }
        }



    }
}
