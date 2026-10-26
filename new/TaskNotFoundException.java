/** Thrown when an operation references a task ID that does not exist. */
public class TaskNotFoundException extends TaskException {
    public TaskNotFoundException(String taskId) {
        super("No task found with ID '" + taskId + "'");
    }
}
