package listener;

import pojos.TransferToolPKey;
import utils.ByteSerializer;
import utils.EncriptionUtil;

import java.io.*;
import java.net.Socket;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class ListenService extends Thread {

    private Socket socket;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public ListenService(Socket socket, RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        this.socket = socket;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public ListenService(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("new Thread");
        try{
            BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());

            writer.write(1);
            writer.flush();

            //get the sender key
//            byte[] senderPKeyBits = new byte[1000];
//            reader.read(senderPKeyBits);
//
//            //transform the bits to object
//            TransferToolPKey senderPKey = (TransferToolPKey) ByteSerializer.deserializeBytes(senderPKeyBits);
//
//            //create our TransferToolPKey to send to sender
//            TransferToolPKey listenerPKey = new TransferToolPKey(publicKey);
//
//            //send our TransferToolPKey
//            writer.write(ByteSerializer.serializeObject(listenerPKey));
//            writer.flush();
//
//            System.out.println("keys shared");
//            System.out.println("sender key = " + senderPKey.getPublicKey().getPublicExponent());

            System.out.println("finished conn");





        }catch (Exception e){
            System.err.println("Connection lost");
        }
    }
}
