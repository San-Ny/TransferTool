package exceptions;

public class WrongArgumentException extends Throwable {

    public WrongArgumentException() {
    }

    public WrongArgumentException(String message) {
        super(message);
    }
}
