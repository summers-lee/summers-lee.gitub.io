/**
 * Base type for every error raised by the task-management module.
 * Callers can catch this one type to handle any task-related failure,
 * or catch a specific subclass to react to a particular condition.
 */
public class TaskException extends RuntimeException {
    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
