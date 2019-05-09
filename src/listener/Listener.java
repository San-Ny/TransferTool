package listener;

import utils.ConfigurationUtil;
import utils.ScannerUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 *
 * @deprecated
 */

public class Listener {
    public static void main(String[] args) {
        //check conf file
        if (!ConfigurationUtil.isConfigPresent())ConfigurationUtil.generateConf();

        String port = ConfigurationUtil.getPropertyOrDefault("ListenerPort", "9990");

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))){
            while(true){
                //listening
                Socket newSocket = serverSocket.accept();

                //obtaining newSocket info and checking config file
                String incomingConnections = ConfigurationUtil.getPropertyOrDefault("IncomingConnections", "Filtered");
                InetSocketAddress inetSocketAddress = (InetSocketAddress)newSocket.getRemoteSocketAddress();

                if (incomingConnections.equals("Verbose")){
                    if (!ScannerUtil.getVerboseInput("Allow incoming connection of: " + inetSocketAddress.getHostString() + "<" + inetSocketAddress.getHostName() + "> [Y/n]")){
                        System.err.println("Connexion closed!");
                        newSocket.close();
                        continue;
                    }
                }

                if (incomingConnections.equals("Filtered")){
                    String trusted = ConfigurationUtil.getPropertyOrDefault("TrustedHosts", "(^127\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))|(^192\\.168\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))");
                    if (!inetSocketAddress.getHostString().matches(trusted)){
                        System.err.println(inetSocketAddress.getHostString() + " untrusted Host, connection not allowed.");
                        newSocket.close();
                        continue;
                    }
                }

                System.out.println("New connexion from " + inetSocketAddress.getHostString());

                ListenService listenService = new ListenService(newSocket);
                listenService.start();
            }
        }catch (IOException e){
            System.exit(-1);
        }
    }
}
