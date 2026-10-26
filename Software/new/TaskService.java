import java.util.List;

/**
 * Business logic for managing tasks. Storage is delegated to a
 * TaskRepository that is supplied through the constructor, so this class
 * neither knows nor cares whether tasks live in memory or on disk.
 */
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.repository = repository;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new InvalidTaskDataException("Task must not be null");
        }
        repository.add(task);
    }

    public void deleteTask(String taskId) {
        repository.delete(taskId);
    }

    public void updateName(String taskId, String name) {
        Task task = repository.findById(taskId);
        task.setName(name);          // validates before anything is stored
        repository.update(task);
    }

    public void updateDescription(String taskId, String description) {
        Task task = repository.findById(taskId);
        task.setDescription(description);
        repository.update(task);
    }

    public Task getTask(String taskId) {
        return repository.findById(taskId);
    }

    public List<Task> getAllTasks() {
        return repository.findAll();
    }
}
