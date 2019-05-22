package utils;

import com.jcraft.jsch.Session;

public class ConsolePrinterUtil {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void println(String msg){
        System.out.println(msg);
    }

    public static void print(String msg){
        System.out.print(msg);
    }

    public static void printClassInfo(Class who,String msg){
        System.out.format("%s[%s%s%s]%s: %s",ANSI_YELLOW, ANSI_PURPLE, who.getName(), ANSI_YELLOW, ANSI_RESET, msg);
    }

    public static void printDebugging(Class who, String msg, int line){
        System.out.format("%s[%s%s%s line %s%d%s]%s: %s\n",ANSI_YELLOW, ANSI_BLUE, who.getName(), ANSI_RESET, ANSI_RED, line, ANSI_YELLOW, ANSI_RESET, msg);
    }

    /**
     *
     * @param msg
     * @param status
     */
    public static void die(String msg, int status){
        System.err.println(msg);
        System.exit(status);
    }
    public static void die(Class who, Session session, String msg, int status){
        session.disconnect();
        printClassInfo(who, msg);
        System.exit(status);
    }
    public static void die(Class who, String msg, int status){
        printClassInfo(who, msg);
        System.exit(status);
    }
    public static void die(Class who, String msg, int status, int line){
        printDebugging(who, msg, line);
        System.exit(status);
    }
}
