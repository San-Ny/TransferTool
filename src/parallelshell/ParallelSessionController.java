package parallelshell;

import pojos.Host;
import utils.ArgumentReaderUtil;
import utils.ConsolePrinterUtil;
import utils.ScannerUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.ConsolePrinterUtil.*;
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
        if (!ArgumentReaderUtil.isOneValid(properties, requiredProperties)) die(ParallelSessionController.class,"Missing required arguments", 0);


        //assignation
        ArrayList<Host> hosts = new ArrayList<>();
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        if (properties.containsKey("Interactive") && properties.getProperty("Interactive").equals("1")) {
            //inserting hosts
            while (true){
                String option = getLine("  1 Insert Host\n  2 Remove host\n  3 list hosts\n  4 Finish\n  5 Cancel\n" + getCommandInput());
                if (option.equals("1")){
                    hosts.add(new Host(getLine("User:"), getLine("Host:"), getLine("port:"), hosts.size() - 1));
                }else if (option.equals("2")){
                    AtomicInteger a = new AtomicInteger();
                    hosts.forEach((v) -> {
                        ConsolePrinterUtil.println( a.getAndIncrement() + " " + v.toString());
                    });
                    hosts.remove(getInt("Number:"));
                }else if (option.equals("3")) hosts.forEach((v) -> ConsolePrinterUtil.println(v.toString()));
                else if (option.equals("4")) break;
                else if (option.equals("5")) die("bye", 0);
            }
        }else{
            try (BufferedReader bf = new BufferedReader(new FileReader(properties.getProperty("fileLocal")))) {
                String line;
                int position = 0;
                while ((line = bf.readLine()) != null){
                    String[] data = line.split("[@:]");
                    if (data.length != 3) {
                        ConsolePrinterUtil.println("Wrong format:'" + line + "' ignoring host");
                        continue;
                    }
                    hosts.add(new Host(data[0], data[1], data[2], position++));
                }
            } catch (IOException e) {
//                e.printStackTrace();
                die("File not found", -1);
            }
        }

        if (hosts.size() < 2) ConsolePrinterUtil.die("Minimum required hosts 2", 0);

        ArrayList<ActiveShell> activeShells = new ArrayList<>();

        hosts.forEach((k) -> {
            activeShells.add(new ActiveShell(k));
        });

        while (true){
            String command = ScannerUtil.getLine("Command: ");
            if (command.equals("stop pssh")) {
                activeShells.forEach(ActiveShell::stop);
                break;
            } else activeShells.forEach((k) -> k.exec(command));
        }
    }
}
