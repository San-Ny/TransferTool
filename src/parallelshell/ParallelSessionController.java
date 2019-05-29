package parallelshell;

import utils.ArgumentReaderUtil;
import utils.ConsolePrinterUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        String[] requiredProperties = {"fileLocal", "Interactive"};
        if (ArgumentReaderUtil.isOneValid(properties, requiredProperties)) die(ParallelSessionController.class,"Missing required arguments", 0);


        //assignation
        HashMap<String, String> hosts = new HashMap<>();
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
            //inserting hosts
            while (true){
                String option = getLine("  1 Insert Host\n  2 Remove host\n  3 list hosts\n  4 Finish\n  5 Cancel\n" + getCommandInput());
                if (option.equals("1")){
                    hosts.put(getLine("User:"), getLine("Host:"));
                }else if (option.equals("2")){
                    hosts.remove(getLine("User:"), getLine("Host:"));
                }else if (option.equals("3")) hosts.forEach((v, k) -> ConsolePrinterUtil.println("\t\tU:" + v + "\t\tH:" + k));
                else if (option.equals("4")) break;
                else if (option.equals("5")) die("bye", 0);
            }
        }else{
            try (BufferedReader bf = new BufferedReader(new FileReader(properties.getProperty("fileLocal")))) {
                String line;
                while ((line = bf.readLine()) != null){
                    String[] data = line.split("[@]");
                    if (data.length != 2) {
                        ConsolePrinterUtil.println("Wrong format:'" + line + "' ignoring host");
                        continue;
                    }
                    hosts.put(data[0], data[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (hosts.size() < 1){
            ConsolePrinterUtil.die("Empty list", 0);
        }




    }
}
