package sender;

import pojos.EncriptionUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * @serial   mit https://mit-license.org/
 * @link     san.data.ddns.net
 */

public class Sender {

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

        if (args.length < 2 || args.length > 4) System.exit(-1);

        String host = "";
        String port = "9555";

        for (int a = 0; a < args.length; a++){
            if (args[a].equals("-R")) host = args[a + 1];
            else if (args[a].equals("-p")) port = args[a + 1];
            else if (args[a].equals("-W")){
                String[] hpCommand = args[a + 1].split(":");
                if (hpCommand.length != 2) System.exit(-1);
                host = hpCommand[0];
                port = hpCommand[1];
            }
        }

        if (port.equals("")) System.exit(-1);


        //certificates

        PublicKey publicKey = null;
        PrivateKey privateKey = null;
        try{
            // generates the keys if not eists
            if (!EncriptionUtil.areKeysPresent()) EncriptionUtil.generateKey();

            //key file reader
            ObjectInputStream inputStream;

            // gets the public key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PUBLIC_KEY_FILE));
            publicKey = (PublicKey) inputStream.readObject();

            // gets the private key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PRIVATE_KEY_FILE));
            privateKey = (PrivateKey) inputStream.readObject();

        }catch (Exception e){
            System.err.println("Certificates error");
            System.exit(-1);
        }

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))){
            while(true){
                Socket newSocket = serverSocket.accept();
                SendService sendService = new SendService(newSocket, privateKey, publicKey);
                sendService.start();
            }
        }catch (IOException e){
            System.err.println(e.getMessage());
        }


    }
}
