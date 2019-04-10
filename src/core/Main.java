package core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * @serial   mit https://mit-license.org/
 * @link     san.data.ddns.net
 */

public class Main {

    /**
     * Core program, gets the arguments and deploy the requested task
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        //easy transfer files and folders

        //-p port
        //-R host
        //-W host:port

        if (args.length < 2 || args.length > 4) error(true, "incorrect params", -1);

        String host = "";
        String port = "9555";

        for (int a = 0; a < args.length; a++){
            if (args[a].equals("-R")) host = args[a + 1];
            else if (args[a].equals("-p")) port = args[a + 1];
            else if (args[a].equals("-W")){
                String[] hpCommand = args[a + 1].split(":");
                if (hpCommand.length != 2) error(true, "core -> Format is 'host:port'", -1);
                host = hpCommand[0];
                port = hpCommand[1];
            }
        }

        if (port.equals("")) System.out.println("core -> The host is not specified, waiting for incoming connection");
        else System.out.format("core -> starting connection with %s, current port %s\n", host, port);

        Listener listener = new Listener(host, port);
        listener.start();
        try {
            listener.join();
        }catch (InterruptedException e){
            error(true,"listening service interrupted unexpectedly", -1);
        }

        while (true){

        }


    }


    /**
     *
     * Function to print messages or fatal errors
     *
     * @param error gets am error and finishes
     * @param msg gets the message to print
     * @param status gets the finish status if error is true
     */
    private static void error(boolean error, String msg, int status){
         if (error) System.err.println(msg);
         else System.out.println(msg);
         System.exit(status);
    }
}
