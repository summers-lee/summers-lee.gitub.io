import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    // ── addTask ───────────────────────────────────────────────────────────────

    @Test
    void testAddTaskSucceeds() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task); // should not throw
    }

    @Test
    void testAddDuplicateTaskThrows() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        assertThrows(IllegalArgumentException.class, () -> service.addTask(task));
    }

    @Test
    void testAddNullTaskThrows() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.addTask(null));
    }

    @Test
    void testAddMultipleTasksWithUniqueIds() {
        TaskService service = new TaskService();
        Task task1 = new Task("1", "Task One", "First task description here for testing");
        Task task2 = new Task("2", "Task Two", "Second task description here for testing");
        service.addTask(task1);
        service.addTask(task2); // should not throw
    }

    // ── deleteTask ────────────────────────────────────────────────────────────

    @Test
    void testDeleteTaskSucceeds() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        service.deleteTask("1"); // should not throw
    }

    @Test
    void testDeleteTaskThenAddAgainSucceeds() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        service.deleteTask("1");
        service.addTask(task); // ID freed — should not throw
    }

    @Test
    void testDeleteNonExistentTaskThrows() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.deleteTask("999"));
    }

    @Test
    void testDeleteAlreadyDeletedTaskThrows() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        service.deleteTask("1");
        assertThrows(IllegalArgumentException.class, () -> service.deleteTask("1"));
    }

    // ── updateName ────────────────────────────────────────────────────────────

    @Test
    void testUpdateNameSucceeds() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        service.updateName("1", "Return Items");
        // No exception = update accepted; verify via the task object
        service.updateName("1", "Buy Groceries"); // round-trip check — no exception
    }

    @Test
    void testUpdateNameInvalidIdThrows() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () ->
            service.updateName("999", "New Name"));
    }

    @Test
    void testUpdateNameNullThrows() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        assertThrows(IllegalArgumentException.class, () ->
            service.updateName("1", null));
    }

    @Test
    void testUpdateNameTooLongThrows() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        assertThrows(IllegalArgumentException.class, () ->
            service.updateName("1", "123456789012345678901")); // 21 chars
    }

    // ── updateDescription ─────────────────────────────────────────────────────

    @Test
    void testUpdateDescriptionSucceeds() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        service.updateDescription("1", "Grab items from aisle 5 and checkout lane 2");
        service.updateDescription("1", "Pick up milk, eggs, and bread"); // round-trip — no exception
    }

    @Test
    void testUpdateDescriptionInvalidIdThrows() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () ->
            service.updateDescription("999", "Some description"));
    }

    @Test
    void testUpdateDescriptionNullThrows() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        assertThrows(IllegalArgumentException.class, () ->
            service.updateDescription("1", null));
    }

    @Test
    void testUpdateDescriptionTooLongThrows() {
        TaskService service = new TaskService();
        Task task = new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
        service.addTask(task);
        assertThrows(IllegalArgumentException.class, () ->
            service.updateDescription("1", "123456789012345678901234567890123456789012345678901")); // 51 chars
    }
}