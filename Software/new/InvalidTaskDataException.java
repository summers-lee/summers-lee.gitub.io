/** Thrown when task data (or a null task) fails validation. */
public class InvalidTaskDataException extends TaskException {
    public InvalidTaskDataException(String message) {
        super(message);
    }
}
