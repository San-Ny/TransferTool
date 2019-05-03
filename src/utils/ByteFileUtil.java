package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class ByteFileUtil {

    /**
     * write a file from an byte array
     * @param to file path
     * @param data byte array
     * @throws FileNotFoundException If file is not present
     * @throws IOException general exceptions
     */
    public static void writeBytes(String to, byte[] data) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(to)){
            for (byte b : data) out.write(b);
        }
    }

    /**
     * get bytes from a file
     * @param from file path
     * @return return the byte array
     * @throws FileNotFoundException file not found
     * @throws IOException general exceptions
     */
    public static byte[] getBytes(String from) throws FileNotFoundException, IOException {
        List<Byte> bytes = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(from)) {
            int c;
            while ((c = in.read()) != -1) bytes.add((byte) c);
        }
        return ByteSerializer.serializeObject(bytes.toArray());
//        byte[] data = new byte[bytes.size()];
//        int i = 0;
//        for (Byte b : bytes) data[i++] = b;
//        return data;
    }

    /**
     * write object
     * @param object file as object
     * @param to path
     * @throws FileNotFoundException file not found
     * @throws IOException general exceptions
     */
    public static void writeObject(Object object, String to) throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(to)))) {
            out.writeObject(object);
        }
    }

    /**
     * return object
     * @param from path
     * @return  file as object
     * @throws ClassNotFoundException Class not found
     * @throws IOException general exceptions
     */
    public static Object getObject(String from) throws ClassNotFoundException, IOException {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(from)))) {
            return in.readObject();
        }
    }
}
