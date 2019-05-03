package utils;

import java.io.*;
import java.util.ArrayList;

public class ByteFileUtil {

    public static void writeBytes(String desti, byte[] dades) throws FileNotFoundException, IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(desti);
            for (byte b : dades) {
                out.write(b);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static byte[] getBytes(String origen) throws FileNotFoundException, IOException {
        ArrayList<Byte> bytes = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(origen)) {
            int c;
            while ((c = in.read()) != -1) {
                bytes.add((byte) c);
            }
        }
        byte[] dades = new byte[bytes.size()];
        int i = 0;
        for (Byte b : bytes) {
            dades[i++] = b;
        }
        return dades;
    }

    public static void writeObject(Serializable objecte, String desti) throws FileNotFoundException, IOException {

        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(desti)))) {

            out.writeObject(objecte);
        }
    }

    public static Object getObject(String origen) throws ClassNotFoundException, IOException {
        Object dades = null;
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(origen)))) {

            while (true) {
                dades = in.readObject();
            }
        } catch (EOFException e) {
            System.out.println("EOF");
        } finally {
            return dades;
        }
    }
}
