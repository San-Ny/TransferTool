package pojos;

import java.io.Serializable;
import java.security.interfaces.RSAPublicKey;


/**
 * @deprecated
 */
public class TransferToolPKey implements Serializable {

    private final RSAPublicKey publicKey;

    public TransferToolPKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
