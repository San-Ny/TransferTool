package utils;

import exceptions.TransferToolException;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ScannerUtil {

    public static String getLine(){
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public static String getLine(String msg){
        System.out.print(msg);
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public static String  readPassword() throws TransferToolException{
        Console console = System.console();
        try{
            return String.valueOf(console.readPassword("Enter Password: "));
        } catch (Exception e) {
            e.printStackTrace();
            throw new TransferToolException("Unable to get console instance");
        }
    }

    /**
     * method to read password from IDE
     * @return
     * @throws IOException
     * @deprecated
     */
    public static String getPassword() throws IOException {
            System.out.print("Password: ");
            InputStream in=System.in;
            int max=50;
            byte[] b=new byte[max];

            int l= in.read(b);
            l--;
            if (l>0) {
                byte[] e=new byte[l];
                System.arraycopy(b,0, e, 0, l);
                return new String(e);
            } else return null;
    }

    public static boolean getVerboseInput(String msg){
        while (true) {
            System.out.print(msg);
            String line = ScannerUtil.getLine();
            if (line.equals("Y") || line.equals("y") || line.equals("S") || line.equals("s")) {
                return true;
            } else if (line.equals("N") || line.equals("n")) return false;
        }
    }
}
