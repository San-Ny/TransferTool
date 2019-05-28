package tests;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;

public class TestDefaultEncryptionParams {
    public static void main(String[] args) {
        try{
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024); // speedy generation, but not secure anymore
            KeyPair kp = kpg.generateKeyPair();
            RSAPublicKey pubkey = (RSAPublicKey) kp.getPublic();
            RSAPrivateKey privkey = (RSAPrivateKey) kp.getPrivate();

// --- encrypt given algorithm string
            Cipher oaepFromAlgo = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            oaepFromAlgo.init(Cipher.ENCRYPT_MODE, pubkey);
            byte[] ct = oaepFromAlgo.doFinal("is this SPARTAAA?".getBytes(StandardCharsets.UTF_8));

            for(byte b: ct) System.err.print((char)b);
            System.out.println();

// --- decrypt given OAEPParameterSpec
            Cipher oaepFromInit = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            oaepFromInit.init(Cipher.DECRYPT_MODE, privkey, oaepParams);
            byte[] pt = oaepFromInit.doFinal(ct);
            System.out.println(new String(pt, StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
