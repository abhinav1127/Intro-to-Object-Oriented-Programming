/**
 * Represents an Exception that results as the result of an invalid square
 * input, such as 'a9' or 'j1'
 *
 * @author atirath6
 * @version 1.0
 */
public class InvalidSquareException extends RuntimeException {

    /**
     * This is the constructor, and it calls the super constructor with the
     * input message.
     * CHECKED EXCEPTION: Since the person who is using our program can
     * manipulate what rank and file is being constructed, we should make it a
     * checked exception. This will signify to the user that they are able to
     * fix the problem themeselves and that it is not an error in the code.
     * Now, we have changed it to a unchecked excpetion.
     * @param message the message specific to InvalidSquareExceptions
     */
    public InvalidSquareException(String message) {
        super(message);
    }
}
