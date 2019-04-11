package pojos;

import java.security.PublicKey;

public class TransferToolPKey {

    private final PublicKey publicKey;

    public TransferToolPKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
