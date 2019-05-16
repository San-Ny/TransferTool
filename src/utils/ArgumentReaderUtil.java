package utils;

import exceptions.WrongArgumentException;
import java.util.Properties;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class ArgumentReaderUtil {

    /**
     * gets an array
     * @param args user input as array
     * @return Properties with the arguments inserted
     * @throws WrongArgumentException if arguments are invalid or bad filled
     */
    public static Properties getParams(String[] args) throws WrongArgumentException {

        //-u user
        //-p port
        //-R host
        //-r recursive
        //-s strict mode ------------------
        //-t generates new dir on remote host
        //-v verbose
        //-d debugging
        //-W host:port
        //-J [user@]host[:port]
        //-fl files local
        //-fr files remote
        //-ssh via ssh
        //-sftp via sftp

        if (args.length < 4) {
            System.out.println("Invalid arguments.");
            System.exit(0);
        }

        //check conf file
        if (!ConfigurationUtil.isConfigPresent())ConfigurationUtil.generateConf();

        //load default parameters
        Properties properties = ConfigurationUtil.getParams();
        if (properties == null) properties = new Properties();

        //read arguments and overwrite def parameters
        for (int a = 0; a < args.length; a++){
            if (args[a].equals("-R")) properties.put("host",args[++a]);
            else if (args[a].equals("-p")) properties.put("port",args[++a]);
            else if (args[a].equals("-fl")) properties.put("fileLocal",args[++a]);
            else if (args[a].equals("-fr")) properties.put("fileRemote",args[++a]);
            else if (args[a].equals("-ssh")) properties.put("Method", "ssh");
            else if (args[a].equals("-sftp")) properties.put("Method", "sftp");
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
            }else if (args[a].equals("-u")) properties.put("user",args[++a]);
            else if (args[a].equals("-r"))  properties.put("recursive", "1");
            else if (args[a].equals("-v"))  properties.put("verbose", "1");
            else if (args[a].equals("-d"))  properties.put("Debugging", "ON");
            else{
                properties.put("Debugging","OFF");
                properties.put("recursive", "0");
                properties.put("verbose", "0");
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

        if (properties.getProperty("Debugging").equals("1")) properties.forEach((k, v) -> System.out.println("ArgumentReaderUtil -> " + k + ":" + v));

        return properties;
    }

    /**
     * method will get properties and the name of required parameters
     * @param properties propertied collected from arguments and config file
     * @param requiredValues Array of required values
     * @return return if properties document is valid or not
     */
    public static boolean isValid(Properties properties, String[] requiredValues){
        for(String s : requiredValues) if (!properties.containsKey(s)) return false;
        return true;
    }

    public static boolean isOneValid(Properties properties, String[] requiredValues){
        for(String s : requiredValues) if (properties.containsKey(s)) return true;
        return false;
    }
}
