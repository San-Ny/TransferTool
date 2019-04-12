package sender;

import pojos.TransferToolPKey;
import utils.ByteSerializer;
import utils.EncriptionUtil;
import utils.ScannerUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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

        RSAPublicKey publicKey = null;
        RSAPrivateKey privateKey = null;
        try{
            // generates the keys if not eists
            if (!EncriptionUtil.areKeysPresent()) EncriptionUtil.generateKey();

            //key file reader
            ObjectInputStream inputStream;

            // gets the public key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PUBLIC_KEY_FILE));
            publicKey = (RSAPublicKey) inputStream.readObject();

            // gets the private key
            inputStream = new ObjectInputStream(new FileInputStream(EncriptionUtil.PRIVATE_KEY_FILE));
            privateKey = (RSAPrivateKey) inputStream.readObject();

        }catch (Exception e){
            System.err.println("Certificates error");
            System.exit(-1);
        }

        try(Socket senderSocket = new Socket()){
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host,Integer.parseInt(port));
            senderSocket.connect(inetSocketAddress);

            //starting writer and reader
            BufferedInputStream reader = new BufferedInputStream(senderSocket.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(senderSocket.getOutputStream());

            //get senderPKey bits
            byte[] senderPKey = ByteSerializer.serializeObject(new TransferToolPKey(publicKey));


            //send sender key to other pc
            writer.write(senderPKey);
            writer.flush();

            byte[] listenerPKeyBits = new byte[senderPKey.length];
            int result = reader.read(listenerPKeyBits);

            //transform the bits to object
            TransferToolPKey listenerPKey = null;
            try{
                listenerPKey = (TransferToolPKey) ByteSerializer.deserializeBytes(listenerPKeyBits);
            }catch (ClassNotFoundException e){
                System.err.println("class not found");
                System.exit(-1);
            }

            System.out.println("connected");

            byte[] message = new byte[100];
            reader.read(message);
            String decriptedMsg = EncriptionUtil.decrypt(message,privateKey);
            System.out.println(decriptedMsg);

        }catch (IOException e){
            System.err.println(e.getMessage());
        }


    }
}
