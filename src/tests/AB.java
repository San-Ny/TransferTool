package tests;

import exceptions.TransferToolException;
import utils.EncryptionUtil;

import java.io.*;
import java.security.PrivateKey;

public class AB {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        Server server = new Server();
//        Sender sender = new Sender();
//
//        // listener server, service
//        server.start();
//
//        // incoming connection
//        sender.start();
//
//        try{
//            server.join();
//            sender.join();
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(EncryptionUtil.PRIVATE_KEY_PATH));
        final PrivateKey privateKey = (PrivateKey) inputStream.readObject();

//        byte[] data = {96, -96, -7, 76, 3, -125, -15, 8, 58, -33, -47, 20, 99, -24, -55, 46, 10, -88, 56, 113, -35, -37, -125, -112, 27, -119, -9, -20, 87, -36, -39, 5, 105, 63, 10, -70, -98, 108, -78, -70, 71, 85, 31, 108, 121, -61, 13, 50, 39, 15, 42, 114, 83, 127, -42, 111, 125, 108, -8, -95, -92, 84, 48, 126, -115, 90, 76, 110, -77, -54, 78, 79, -101, -68, -58, 9, 126, -117, -83, 59, 35, 107, 25, -53, -71, 98, -116, 121, -119, 72, 31, 75, 67, 13, -75, 115, 94, -80, 110, 86, 31, -60, -86, 36, -60, -124, 41, 64, -27, 88, -82, 65, 46, 48, -43, 107, -11, 63, 99, 37, 69, -90, -13, -24, -114, 111, 73, 84, 124, -99, 35, 58, 91, -123, 4, 47, 6, -9, 22, -23, -83, 119, -18, -34, -71, 27, -82, -63, -20, -26, 58, -60, 113, 28, -16, 16, -31, 71, 68, -121, -2, 116, 109, 100, 59, -47, 42, -77, -24, 28, -122, 35, -70, 87, 96, 20, 3, -58, 102, 112, 83, -88, 15, -56, 86, 12, 95, 5, -98, -50, 82, 92, -50, 14, 24, 14, 102, 124, -80, 84, 16, -88, 124, -126, -110, -118, 90, -114, 75, 100, 46, 52, -119, -96, 34, -85, -24, -123, -1, -60, 97, -24, 80, 5, -53, -11, -79, 55, -97, -48, 50, 109, -14, 30, -123, 79, 24, -21, 50, 73, 30, -83, 53, -55, 122, 3, 32, -117, -114, -57, 32, -48, 17, -59, 32, 7};
        byte[] data = {55, -9, -118, -27, -29, 98, -25, 38, 119, 12, 121, 77, 115, -63, -63, -100, 67, 72, 21, -85, -66, 88, 21, 16, -6, -68, -118, -39, 36, -118, 122, -94, -39, 112, 33, 42, 93, 115, -8, 54, -25, -102, -88, 91, 92, 29, 90, 3, -51, 35, 43, 63, 98, -80, 94, 17, -24, -121, 72, -122, 26, 87, -47, -110, 22, -114, 7, 92, -91, 113, -109, -39, 13, 74, -33, 45, 35, -116, -44, -87, 44, 20, 122, -106, 125, -20, -11, 48, 4, -94, 49, 85, 42, 84, 100, -83, 114, 42, -51, -65, -51, 27, 85, -89, 18, -116, -79, 20, -94, -100, -14, 22, 8, 92, -93, -104, 42, -33, 42, -96, 75, 104, -71, -117, -55, 65, -124, 44, -56, -103, -1, 12, -32, 116, 83, 14, -65, 11, -75, 20, -32, -101, 117, -29, -83, 96, 18, 23, 43, -41, -96, -44, -96, -107, 90, -26, -42, 105, -55, 99, -95, -51, 8, 63, 91, -64, 77, -83, -112, 123, -4, -62, -119, -43, -90, -46, -86, -25, 19, -26, 54, -6, 12, 42, 13, -116, 31, 13, 2, 37, -77, 94, 90, 90, 49, 61, 1, 121, 82, -12, 20, -114, 117, -91, -58, -18, 12, 46, 77, -18, 11, 23, 122, -72, -30, 42, -63, 7, -113, -59, 17, 31, 108, -10, 114, -24, -88, -9, 48, -46, 117, 73, 81, 97, 106, 65, 3, 29, -117, -70, -118, -32, -57, -89, -12, 76, 59, -10, 119, 99, 115, 4, -86, 99, 59, -70};
        byte[] retornArxiu = new byte[0];
        try {
            retornArxiu = EncryptionUtil.decrypt(data, privateKey);
        } catch (TransferToolException e) {
            e.printStackTrace();
        }
        for (int i:retornArxiu) System.err.print((char)i);
    }
}
