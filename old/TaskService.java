import java.util.HashMap;
import java.util.Map;

public class TaskService {
    private final Map<String, Task> tasks = new HashMap<>();

    public void addTask(Task task) {
        if (task == null || tasks.containsKey(task.getTaskId())) {
            throw new IllegalArgumentException("Task is null or ID already exists");
        }
        tasks.put(task.getTaskId(), task);
    }

    public void deleteTask(String taskId) {
        if (!tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("Task ID not found");
        }
        tasks.remove(taskId);
    }

    public void updateName(String taskId, String name) {
        getTask(taskId).setName(name);
    }

    public void updateDescription(String taskId, String description) {
        getTask(taskId).setDescription(description);
    }

    private Task getTask(String taskId) {
        if (!tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("Task ID not found");
        }
        return tasks.get(taskId);
    }
}