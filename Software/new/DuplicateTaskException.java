/** Thrown when adding a task whose ID is already in use. */
public class DuplicateTaskException extends TaskException {
    public DuplicateTaskException(String taskId) {
        super("A task with ID '" + taskId + "' already exists");
    }
}
