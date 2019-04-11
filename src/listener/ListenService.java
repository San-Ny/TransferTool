package listener;

import pojos.TransferToolPKey;

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
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            BufferedInputStream reader = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());

            //get the sender key
            byte[] senderPKeyBits = new byte[50];
            int result = reader.read(senderPKeyBits);

            //transform the bits to object
            TransferToolPKey senderPKey = (TransferToolPKey)deserializeBytes(senderPKeyBits);

            //create our TransferToolPKey to send to sender
            TransferToolPKey listenerPKey = new TransferToolPKey(publicKey);
            writer.write(serializeObject(listenerPKey));
            writer.flush();

            System.out.println("Handshake done, encrypted connection from now on");

        }catch (Exception e){
            System.err.println("transaction successfully failed");
        }
    }

    /**
     * Convert objects to byte arrays
     * @param bytes gets the inputstream bytes
     * @return return an object
     * @throws IOException IOException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static Object deserializeBytes(byte[] bytes) throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bytesIn);
        Object obj = ois.readObject();
        ois.close();
        return obj;
    }

    /**
     * Gets an object and return the bytes
     * @param obj gets the object
     * @return return byte[]
     * @throws IOException IOException
     */
    public static byte[] serializeObject(Object obj) throws IOException
    {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
        oos.writeObject(obj);
        oos.flush();
        byte[] bytes = bytesOut.toByteArray();
        bytesOut.close();
        oos.close();
        return bytes;
    }
}
