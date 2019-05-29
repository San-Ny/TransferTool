package utils;

import exceptions.TransferToolException;
import java.io.Console;
import java.util.Scanner;

import static utils.ConsolePrinterUtil.*;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
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

    public static int getInt(){
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }
    public static int getInt(String msg){
        print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.nextInt();
    }

    public static String getPath(String path) {
        Scanner sc = new Scanner(System.in);
        while (true){
            print(path);
            String line = sc.nextLine();
            if (PathFinderUtil.isValidPath(line)) return line;
            printEln("Invalid path");
        }
    }

    /**
     * get scanner line input printing a message
     * @param msg message to show  before reading
     * @return String with the input
     */
    public static String getLine(String msg){
        print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /**
     * get scanner line input printing a message
     * @param msg message to show  before reading
     * @return String with the input
     */
    public static String[] getLineAsArray(String msg, String regex){
        print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine().split(regex);
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

    /**
     * show message and compare in put in an valid or invalid array, if not found will be asked again
     * @param msg message to show
     * @param valid valid inputs -> confirm
     * @param invalid invalid inputs -> denied
     * @return return if confirmed or denied
     */
    public static boolean getVerboseInput(String msg, String[] valid, String[] invalid){
        while (true) {
            String line = ScannerUtil.getLine(msg);
            for (String val:valid)if (val.equals(line)) return true;
            for (String val:invalid)if (val.equals(line)) return false;
        }
    }
}
