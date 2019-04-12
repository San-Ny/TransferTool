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

    @Override
    public void run() {
        super.run();

        try{
            BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());

            //get the sender key
            byte[] senderPKeyBits = new byte[1000];
            reader.read(senderPKeyBits);

            //transform the bits to object
            TransferToolPKey senderPKey = (TransferToolPKey) ByteSerializer.deserializeBytes(senderPKeyBits);

            //create our TransferToolPKey to send to sender
            TransferToolPKey listenerPKey = new TransferToolPKey(publicKey);

            //send our TransferToolPKey
            writer.write(ByteSerializer.serializeObject(listenerPKey));
            writer.flush();



        }catch (Exception e){
            System.err.println("transaction successfully failed");
        }
    }
}
