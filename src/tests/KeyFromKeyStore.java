package tests;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;

public class KeyFromKeyStore {
    public static void main(String[] args) {

        try {

            FileInputStream is = new FileInputStream("/home/mykeystore.jks");
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "myuserpass";
            char[] passwd = password.toCharArray();
            keystore.load(is, passwd);
            String alias = "mykeystore";
            Key key = keystore.getKey(alias, passwd);
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);
                // Get public key
                PublicKey publicKey = cert.getPublicKey();

//                String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
//                System.out.println(publicKeyString);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
