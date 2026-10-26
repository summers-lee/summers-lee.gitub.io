import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository that persists tasks to a text file so they survive restarts.
 *
 * File format: one task per line, fields separated by a tab character
 * (taskId, name, description). Backslash, tab, newline and carriage return
 * inside a field are escaped so any valid task text round-trips safely.
 *
 * Trade-off: the whole file is rewritten on every change. That keeps the
 * implementation simple and the file always consistent (it is written to a
 * temporary file and moved into place), which is appropriate for a small
 * task list but would not scale to large data sets.
 */
public class FileTaskRepository implements TaskRepository {
    private final Path file;
    private final Map<String, Task> tasks = new LinkedHashMap<>();

    public FileTaskRepository(Path file) {
        this.file = file;
        load();
    }

    @Override
    public void add(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            throw new DuplicateTaskException(task.getTaskId());
        }
        tasks.put(task.getTaskId(), task);
        save();
    }

    @Override
    public void delete(String taskId) {
        if (!tasks.containsKey(taskId)) {
            throw new TaskNotFoundException(taskId);
        }
        tasks.remove(taskId);
        save();
    }

    @Override
    public void update(Task task) {
        if (!tasks.containsKey(task.getTaskId())) {
            throw new TaskNotFoundException(task.getTaskId());
        }
        tasks.put(task.getTaskId(), task);
        save();
    }

    @Override
    public Task findById(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId);
        }
        return task;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    // ── persistence helpers ──────────────────────────────────────────────

    private void load() {
        if (!Files.exists(file)) {
            return; // first run: nothing stored yet
        }
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\t", -1);
                if (parts.length != 3) {
                    throw new TaskStorageException(
                        "Corrupted task file at line " + (i + 1));
                }
                try {
                    Task task = new Task(unescape(parts[0]), unescape(parts[1]),
                                         unescape(parts[2]));
                    tasks.put(task.getTaskId(), task);
                } catch (InvalidTaskDataException e) {
                    throw new TaskStorageException(
                        "Invalid task data in file at line " + (i + 1), e);
                }
            }
        } catch (IOException e) {
            throw new TaskStorageException("Could not read " + file, e);
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        for (Task t : tasks.values()) {
            lines.add(escape(t.getTaskId()) + "\t" + escape(t.getName())
                      + "\t" + escape(t.getDescription()));
        }
        try {
            Path dir = file.toAbsolutePath().getParent();
            Path temp = Files.createTempFile(dir, "tasks", ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new TaskStorageException("Could not write " + file, e);
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\t", "\\t")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescape(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                switch (next) {
                    case 't': out.append('\t'); break;
                    case 'n': out.append('\n'); break;
                    case 'r': out.append('\r'); break;
                    default:  out.append(next);  break;
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
