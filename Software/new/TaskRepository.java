import java.util.List;

/**
 * Storage abstraction for tasks. TaskService depends on this interface, not
 * on any concrete storage mechanism (Dependency Inversion Principle), so
 * storage can be swapped without touching business logic.
 */
public interface TaskRepository {

    /** Stores a new task. @throws DuplicateTaskException if the ID exists. */
    void add(Task task);

    /** Removes a task. @throws TaskNotFoundException if the ID is unknown. */
    void delete(String taskId);

    /** Persists changes to an existing task. @throws TaskNotFoundException if unknown. */
    void update(Task task);

    /** Returns the task with this ID. @throws TaskNotFoundException if unknown. */
    Task findById(String taskId);

    /** Returns all stored tasks. */
    List<Task> findAll();
}
