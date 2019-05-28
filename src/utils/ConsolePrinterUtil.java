package utils;

import com.jcraft.jsch.Session;

public class ConsolePrinterUtil {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String terminal = " \u001B[32mtp\u001B[0m> ";

    public static final String header = "\n\n" + ANSI_CYAN +
            "\t\t ______________         ______________ \n" +
            "\t\t|_____    _____|       |_____    _____|\n" +
            "\t\t      |  |                   |  |      \n" +
            "\t\t      |  |                   |  |      \n" +
            "\t\t      |  |                   |  |      \n" +
            "\t\t      |  |                   |  |      \n" +
            "\t\t      |  |                   |  |      \n" +
            "\t\t      |__|                   |__|      \n" +
            "\n\n\t\t TransferTool by San-Ny\n" + ANSI_RESET;

    public static final String helpMessage = "\nAvailable parameters:\n\nMethod to transfer files:\n" +
            "\t-scp [null] <Use SSH scp>\n" +
            "\t-sftp [null] <Use SFTP>\n" +
            "\t-pssh [null] <exec/shell on multiple SSH connections simultaneously>\n" +
            "\t-shell [null] <Get shell>\n" +
            "\n" +
            "Available arguments, usage depends of method:\n" +
            "\t--command\t\t\t\t[parameters]\t\t<description>\n" +
            "\t-u\tor --user\t\t\t[user]\t\t\t\t<SSH/SFTP/Shell/Cluster User>\n" +
            "\t-p\tor --port\t\t\t[port]\t\t\t\t<SSH/SFTP/Shell/Cluster Port>\n" +
            "\t-R\tor --host\t\t\t[host]\t\t\t\t<SSH/SFTP/Shell/Cluster url/ip>\n" +
            "\t-r\tor --recursive\t\t\t\t\t\t\t<Recursive>\n" +
            "\t-s\tor --strict\t\t\t\t\t\t\t\t<strict mode on>\n" +
            "\t-w\tor --unstrict\t\t\t\t\t\t\t<strict mode off>\n" +
            "\t-t\t\t\t\t\t\t\t\t\t\t\t<generates new dir on remote host>\n" +
            "\t-v\tor --verbose\t\t\t\t\t\t\t<Verbose>\n" +
            "\t-d\tor --debugging\t\t\t\t\t\t\t<Debugging>\n" +
            "\t-W\t\t\t\t\t\t[host:]port\t\t\t<SSH/SFTP/Shell/Cluster User:Port>\n" +
            "\t-J\t\t\t\t\t\t[user@]host[:port]\t<SSH/SFTP/Shell/Cluster User@host:port>\n" +
            "\t-fl\tor --filelocal\t\t[path]\t\t\t\t<Local directory>\n" +
            "\t-fr\tor --fileremote\t\t[path]\t\t\t\t<Remote directory>\n" +
            "\t-up\tor --upload\t\t\t[path]:[path]\t\t<Upload local file path to remote path>\n" +
            "\t-gt\tor --download\t\t[path]:[path]\t\t<Download remote file path to local file path>\n" +
            "\t-h\tor --help\t\t\t\t\t\t\t\t<Help message>\n" +
            "\t-i\tor --interactive\t\t\t\t\t\t\t<Help message>";

    public static final String helpLiveMessage = "\nMethod to transfer files:\n" +
            "\t scp <Use SSH scp>\n" +
            "\t sftp <Use SFTP>\n" +
            "\t pssh <exec/shell on multiple SSH connections simultaneously>\n" +
            "\t shell <Get shell>\n";

    public static void println(String msg){
        System.out.println(msg);
    }

    public static void print(String msg){
        System.out.print(msg);
    }

    public static void printEln(String msg){
        System.err.println(msg);
    }

    public static void printE(String msg){
        System.err.print(msg);
    }

    public static void printClassInfo(Class who,String msg){
        System.out.format("%s[%s%s%s]%s: %s\n",ANSI_YELLOW, ANSI_PURPLE, who.getName(), ANSI_YELLOW, ANSI_RESET, msg);
    }

    public static void printDebugging(Class who, String msg, int line){
        System.out.format("%s[%s%s%s line %s%d%s]%s: %s\n",ANSI_YELLOW, ANSI_BLUE, who.getName(), ANSI_RESET, ANSI_RED, line, ANSI_YELLOW, ANSI_RESET, msg);
    }

    public static String getCommandInput(){
        return terminal;
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

    public static void printHelp(int status){
            println(helpMessage);
            System.exit(status);
    }
    public static void printHelp(){
            println(helpMessage);
    }
    public static void printLiveHelp(){
            println(helpLiveMessage);
    }
}
