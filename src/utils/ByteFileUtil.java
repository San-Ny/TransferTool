package utils;

import java.io.*;
import java.nio.file.Path;
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
     * @param in byte array
     * @throws FileNotFoundException If file is not present
     * @throws IOException general exceptions
     */
    public static void writeFileFromStream(Path to, FileInputStream in) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(new File(to.toString()))){
            for (byte b : in.readAllBytes()) out.write(b);
        }
    }

    /**
     * get bytes from a file
     * @param from file path
     * @return return the byte array
     * @throws FileNotFoundException file not found
     * @throws IOException general exceptions
     */
    public static FileOutputStream getStream(Path from) throws FileNotFoundException, IOException {
         return new FileOutputStream(from.toString());
    }
}
