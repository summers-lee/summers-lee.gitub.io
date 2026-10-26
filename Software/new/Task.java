/**
 * A single task with a validated ID, name, and description.
 *
 * Validation rules live in one place (the private validate methods) and are
 * shared by the constructor and the setters, so a rule change only needs to
 * be made once.
 */
public class Task {
    static final int MAX_ID_LENGTH = 10;
    static final int MAX_NAME_LENGTH = 20;
    static final int MAX_DESCRIPTION_LENGTH = 50;

    private final String taskId;
    private String name;
    private String description;

    public Task(String taskId, String name, String description) {
        validateTaskId(taskId);
        validateName(name);
        validateDescription(description);

        this.taskId = taskId;
        this.name = name;
        this.description = description;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    private static void validateTaskId(String taskId) {
        if (taskId == null || taskId.length() > MAX_ID_LENGTH) {
            throw new InvalidTaskDataException("Invalid task ID");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.length() > MAX_NAME_LENGTH) {
            throw new InvalidTaskDataException("Invalid name");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidTaskDataException("Invalid description");
        }
    }
}
