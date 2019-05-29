package exceptions;


/**
 * TransferTool
 * @version 0.0.1
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class WrongArgumentException extends Throwable {

    public WrongArgumentException() {
    }

    public WrongArgumentException(String message) {
        super(message);
    }
}
