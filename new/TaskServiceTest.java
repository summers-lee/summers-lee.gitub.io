import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Tests TaskService against the in-memory repository. */
public class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new InMemoryTaskRepository());
    }

    private Task groceries() {
        return new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread");
    }

    // ── constructor ───────────────────────────────────────────────────────────

    @Test
    void testNullRepositoryThrows() {
        assertThrows(IllegalArgumentException.class, () -> new TaskService(null));
    }

    // ── addTask ───────────────────────────────────────────────────────────────

    @Test
    void testAddTaskSucceeds() {
        service.addTask(groceries());
        assertEquals("Buy Groceries", service.getTask("1").getName());
    }

    @Test
    void testAddDuplicateTaskThrows() {
        Task task = groceries();
        service.addTask(task);
        assertThrows(DuplicateTaskException.class, () -> service.addTask(task));
    }

    @Test
    void testAddNullTaskThrows() {
        assertThrows(InvalidTaskDataException.class, () -> service.addTask(null));
    }

    @Test
    void testAddMultipleTasksWithUniqueIds() {
        service.addTask(new Task("1", "Task One", "First task description"));
        service.addTask(new Task("2", "Task Two", "Second task description"));
        assertEquals(2, service.getAllTasks().size());
    }

    // ── deleteTask ────────────────────────────────────────────────────────────

    @Test
    void testDeleteTaskSucceeds() {
        service.addTask(groceries());
        service.deleteTask("1");
        assertThrows(TaskNotFoundException.class, () -> service.getTask("1"));
    }

    @Test
    void testDeleteTaskThenAddAgainSucceeds() {
        Task task = groceries();
        service.addTask(task);
        service.deleteTask("1");
        service.addTask(task); // ID freed — should not throw
        assertEquals(1, service.getAllTasks().size());
    }

    @Test
    void testDeleteNonExistentTaskThrows() {
        assertThrows(TaskNotFoundException.class, () -> service.deleteTask("999"));
    }

    @Test
    void testDeleteAlreadyDeletedTaskThrows() {
        service.addTask(groceries());
        service.deleteTask("1");
        assertThrows(TaskNotFoundException.class, () -> service.deleteTask("1"));
    }

    // ── updateName ────────────────────────────────────────────────────────────

    @Test
    void testUpdateNameSucceeds() {
        service.addTask(groceries());
        service.updateName("1", "Return Items");
        assertEquals("Return Items", service.getTask("1").getName());
    }

    @Test
    void testUpdateNameInvalidIdThrows() {
        assertThrows(TaskNotFoundException.class, () -> service.updateName("999", "New Name"));
    }

    @Test
    void testUpdateNameNullThrowsAndLeavesNameUnchanged() {
        service.addTask(groceries());
        assertThrows(InvalidTaskDataException.class, () -> service.updateName("1", null));
        assertEquals("Buy Groceries", service.getTask("1").getName());
    }

    @Test
    void testUpdateNameTooLongThrowsAndLeavesNameUnchanged() {
        service.addTask(groceries());
        assertThrows(InvalidTaskDataException.class,
            () -> service.updateName("1", "123456789012345678901")); // 21 chars
        assertEquals("Buy Groceries", service.getTask("1").getName());
    }

    // ── updateDescription ─────────────────────────────────────────────────────

    @Test
    void testUpdateDescriptionSucceeds() {
        service.addTask(groceries());
        service.updateDescription("1", "Grab items from aisle 5");
        assertEquals("Grab items from aisle 5", service.getTask("1").getDescription());
    }

    @Test
    void testUpdateDescriptionInvalidIdThrows() {
        assertThrows(TaskNotFoundException.class,
            () -> service.updateDescription("999", "Some description"));
    }

    @Test
    void testUpdateDescriptionNullThrowsAndLeavesDescriptionUnchanged() {
        service.addTask(groceries());
        assertThrows(InvalidTaskDataException.class, () -> service.updateDescription("1", null));
        assertEquals("Pick up milk, eggs, and bread", service.getTask("1").getDescription());
    }

    @Test
    void testUpdateDescriptionTooLongThrowsAndLeavesDescriptionUnchanged() {
        service.addTask(groceries());
        assertThrows(InvalidTaskDataException.class, () ->
            service.updateDescription("1", "123456789012345678901234567890123456789012345678901")); // 51
        assertEquals("Pick up milk, eggs, and bread", service.getTask("1").getDescription());
    }

    // ── exception hierarchy ───────────────────────────────────────────────────

    @Test
    void testAllSpecificExceptionsAreTaskExceptions() {
        assertThrows(TaskException.class, () -> service.deleteTask("999"));
        assertThrows(TaskException.class, () -> service.addTask(null));
    }
}
