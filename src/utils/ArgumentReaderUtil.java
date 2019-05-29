package utils;

import exceptions.WrongArgumentException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class ArgumentReaderUtil {

    /**
     * gets an array
     * @param args user input as array
     * @return Properties with the arguments inserted
     * @throws WrongArgumentException if arguments are invalid or bad filled
     */
    public static Properties getParams(String[] args) throws WrongArgumentException {

        /*
        -scp [null] <Use SSH scp>
        -sftp [null] <Use SFTP>
        -pssh [null] <exec/shell on multiple SSH connections simultaneously>
        -shell [null] <Get shell>
        -u [user] <SSH/SFTP/Shell/Cluster User>
        -p [port] <SSH/SFTP/Shell/Cluster Port>
        -R [host] <SSH/SFTP/Shell/Cluster url/ip>
        -r [null] <Recursive>
        -s [null] <strict mode on>
        -w [null] <strict mode off>
        -t [null] <generates new dir on remote host>
        -v [null] <Verbose>
        -d [null] <Debugging>
        -W [host:]port <SSH/SFTP/Shell/Cluster User:Port>
        -J [user@]host[:port] <SSH/SFTP/Shell/Cluster User@host:port>
        -fl [path] local <Local directory>
        -fr [path] <Remote directory>
        -up [path]:[path] <Upload local file path to remote path>
        -gt [path]:[path] <Download remote file path to local file path>
        -h [null] <Help message>
        -i [null] <Create sessions on interactive menu>
        -encrypt [null] <encript with key par>
        -decrypt [null] <decript with key par>
        */

        if (args.length < 1) ConsolePrinterUtil.die("Null arguments", 0);

        //check conf file
        if (!ConfigurationUtil.isConfigPresent())ConfigurationUtil.generateConf();

        //load default parameters
        Properties properties = ConfigurationUtil.getParams();
        if (properties == null) properties = new Properties();

        //read arguments and overwrite def parameters
        for (int a = 0; a < args.length; a++){
            if (args[a].equals("-R") || args[a].equals("--host")) properties.put("host",args[++a]);
            else if (args[a].equals("-p") || args[a].equals("--port")) properties.put("port",args[++a]);
            else if (args[a].equals("-fl") || args[a].equals("-localFile")) properties.put("fileLocal",args[++a]);
            else if (args[a].equals("-fr") || args[a].equals("-remoteFile")) properties.put("fileRemote",args[++a]);
            else if (args[a].equals("-scp") || args[a].equals("-secureCopy")) properties.put("Method", "scp");
            else if (args[a].equals("-sftp")) properties.put("Method", "sftp");
            else if (args[a].equals("-pssh") || args[a].equals("-parallelShell")) properties.put("Method", "pssh");
            else if (args[a].equals("-shell") || args[a].equals("-ssh")) properties.put("Method", "shell");
            else if (args[a].equals("-h") || args[a].equals("--help")) printHelp();
            else if (args[a].equals("-s") || args[a].equals("-StrictHostKeyCheckingYes")) properties.put("StrictHostKeyChecking", "yes");
            else if (args[a].equals("-w") || args[a].equals("-StrictHostKeyCheckingNo")) properties.put("StrictHostKeyChecking", "no");
            else if (args[a].equals("-W")){
                String[] hpCommand = args[++a].split(":");
                if (hpCommand.length != 2) throw new WrongArgumentException("Wrong parameter on arguments:\n\t-W host[:port]");
                properties.put("host",hpCommand[0]);
                properties.put("port",hpCommand[1]);
            }else if (args[a].equals("-J")){
                String[] hpCommand = args[++a].split("[@:]");
                if (hpCommand.length != 3) throw new WrongArgumentException("Wrong parameter on arguments:\n\t-J [user@]host[:port]");
                properties.put("user",hpCommand[0]);
                properties.put("host",hpCommand[1]);
                properties.put("port",hpCommand[2]);
            }else if (args[a].equals("-u") || args[a].equals("--user")) properties.put("user",args[++a]);
            else if (args[a].equals("-r") || args[a].equals("--recursive"))  properties.put("Recursive", "1");
            else if (args[a].equals("-v") || args[a].equals("--verbose"))  properties.put("Verbose", "1");
            else if (args[a].equals("-i") || args[a].equals("--interactive")) properties.put("Interactive", "1");
            else if (args[a].equals("-encrypt")) properties.put("Method", "encrypt");
            else if (args[a].equals("-decrypt")) properties.put("Method", "decrypt");
            else if (args[a].equals("-d") || args[a].equals("--debugging"))  properties.put("Debugging", "ON");
            else{
                properties.put("Debugging","OFF");
                properties.put("Recursive", "0");
                properties.put("Verbose", "0");
                properties.put("Interactive", "0");
                if (a == 0) properties.put("fileLocal",args[a]);
                else if (a == 1){
                    String[] hpCommand = args[a].split("[@:]");
                    if (hpCommand.length != 3) throw new WrongArgumentException("Wrong default parameters on arguments:\n\t[user@]host[:path]");
                    properties.put("user",hpCommand[0]);
                    properties.put("host",hpCommand[1]);
                    properties.put("fileRemote",hpCommand[2]);
                }else if (a == 2) properties.put("port",args[a]);
                else throw new WrongArgumentException("Unknown default parameters path [path] user@host[:path] port");
            }
        }

        if (properties.containsKey("Debugging") && properties.getProperty("Debugging").equals("ON")) properties.forEach((k, v) -> ConsolePrinterUtil.printDebugging(ArgumentReaderUtil.class,k + ":" + v, Thread.currentThread().getStackTrace()[1].getLineNumber()));

        return properties;
    }

    private static void printHelp() {
        ConsolePrinterUtil.printHelp(0);
    }

    /**
     * method will get properties and the name of required parameters
     * @param properties propertied collected from arguments and config file
     * @param requiredValues Array of required values
     * @return return if properties document is valid or not
     */
    public static boolean isNotValid(Properties properties, String[] requiredValues){
        for(String s : requiredValues) if (!properties.containsKey(s)) return true;
        return false;
    }

    /**
     * check if one of the requested parameters is present
     * @param properties properties to check
     * @param requiredValues array with keys to check
     * @return return true if present else false
     */
    public static boolean isOneValid(Properties properties, String[] requiredValues){
        for(String s : requiredValues) if (properties.containsKey(s)) return true;
        return false;
    }

    /**
     * read a file of hosts format user@host and returns an array map
     * @param path file of hosts
     * @return return all hosts user and hostname
     * @throws FileNotFoundException if file isn't present
     */
    public static HashMap<String, String> getFileHosts(Path path) throws FileNotFoundException {
        BufferedReader file = new BufferedReader(new FileReader(path.toString()));
        HashMap<String, String> hosts = new HashMap<>();
        try (Stream<String> lines = file.lines()) {
            lines.forEach((k) -> {
                String[] data = k.split("@");
                hosts.put(data[0], data[1]);
            });
        }
        return hosts;
    }

    /**
     *
     */
    public static void debugProperties(Properties properties){
        properties.forEach((k, v) -> ConsolePrinterUtil.printDebugging(ArgumentReaderUtil.class,k + ":" + v));
    }
}
