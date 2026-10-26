/**
 * Thrown when a persistence-backed repository cannot read or write its
 * storage (for example an I/O failure or a corrupted data file).
 */
public class TaskStorageException extends TaskException {
    public TaskStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskStorageException(String message) {
        super(message);
    }
}
