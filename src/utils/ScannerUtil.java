package utils;

import java.util.Scanner;

public class ScannerUtil {

    public static String getLine(){
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
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
