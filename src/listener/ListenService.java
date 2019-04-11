package listener;

import pojos.TransferToolPKey;
import utils.ByteSerializer;
import utils.EncriptionUtil;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ListenService extends Thread {

    private Socket socket;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public ListenService(Socket socket, PrivateKey privateKey, PublicKey publicKey) {
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
            byte[] senderPKeyBits = new byte[5000];
            int result = reader.read(senderPKeyBits);

            //transform the bits to object
            TransferToolPKey senderPKey = (TransferToolPKey) ByteSerializer.deserializeBytes(senderPKeyBits);

            //create our TransferToolPKey to send to sender
            TransferToolPKey listenerPKey = new TransferToolPKey(publicKey);

            //send our TransferToolPKey
            writer.write(ByteSerializer.serializeObject(listenerPKey));
            writer.flush();

            System.out.println("Handshake done, encrypted connection from now on");

        }catch (Exception e){
            System.err.println("transaction successfully failed");
        }
    }
}
