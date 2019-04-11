package utils;

import java.io.*;

public class ByteSerializer {

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
