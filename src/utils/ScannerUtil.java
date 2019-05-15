package utils;

import exceptions.TransferToolException;
import java.io.Console;
import java.util.Scanner;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class ScannerUtil {

    /**
     * get scanner line input
     * @return String with the input
     */
    public static String getLine(){
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /**
     * get scanner line input printing a message
     * @param msg message to show  before reading
     * @return String with the input
     */
    public static String getLine(String msg){
        System.out.print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /**
     * read a password console field with the console instance attached to the program
     * @return String with the password
     * @throws TransferToolException Thrown if console instance isn't attached example IDE
     */
    public static String getPassword() throws TransferToolException{
        Console console = System.console();
        try{
            return String.valueOf(console.readPassword("Enter Password: "));
        } catch (Exception e) {
//            e.printStackTrace();
            throw new TransferToolException("Unable to get console instance");
        }
    }

    /**
     * show  message and reads user input Y/y/S/s as true N/n as false, invalid params will be ignored and asked again
     * @param msg message to show before user inserts data
     * @return return response
     */
    public static boolean getVerboseInput(String msg){
        while (true) {
            String line = ScannerUtil.getLine(msg);
            if (line.equals("Y") || line.equals("y") || line.equals("S") || line.equals("s")) {
                return true;
            } else if (line.equals("N") || line.equals("n")) return false;
        }
    }
}
