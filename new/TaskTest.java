import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    // ── Constructor: valid input ──────────────────────────────────────────────

    @Test
    void testValidTask() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        assertEquals("123", task.getTaskId());
        assertEquals("Buy Groceries", task.getName());
        assertEquals("Pick up milk, eggs, and bread", task.getDescription());
    }

    // ── Task ID validation ────────────────────────────────────────────────────

    @Test
    void testNullTaskIdThrows() {
        assertThrows(InvalidTaskDataException.class, () ->
            new Task(null, "Buy Groceries", "Pick up milk, eggs, and bread"));
    }

    @Test
    void testTaskIdTooLongThrows() {
        // 11 characters — over the 10-char limit
        assertThrows(InvalidTaskDataException.class, () ->
            new Task("12345678901", "Buy Groceries", "Pick up milk, eggs, and bread"));
    }

    @Test
    void testTaskIdExactlyTenCharsIsValid() {
        Task task = new Task("1234567890", "Buy Groceries", "Pick up milk, eggs, and bread");
        assertEquals("1234567890", task.getTaskId());
    }

    // ── Name validation ───────────────────────────────────────────────────────

    @Test
    void testNullNameThrows() {
        assertThrows(InvalidTaskDataException.class, () ->
            new Task("123", null, "Pick up milk, eggs, and bread"));
    }

    @Test
    void testNameTooLongThrows() {
        // 21 characters — over the 20-char limit
        assertThrows(InvalidTaskDataException.class, () ->
            new Task("123", "123456789012345678901", "Pick up milk, eggs, and bread"));
    }

    @Test
    void testNameExactlyTwentyCharsIsValid() {
        Task task = new Task("123", "12345678901234567890", "Pick up milk, eggs, and bread");
        assertEquals("12345678901234567890", task.getName());
    }

    // ── Description validation ────────────────────────────────────────────────

    @Test
    void testNullDescriptionThrows() {
        assertThrows(InvalidTaskDataException.class, () ->
            new Task("123", "Buy Groceries", null));
    }

    @Test
    void testDescriptionTooLongThrows() {
        // 51 characters — over the 50-char limit
        assertThrows(InvalidTaskDataException.class, () ->
            new Task("123", "Buy Groceries", "123456789012345678901234567890123456789012345678901"));
    }

    @Test
    void testDescriptionExactlyFiftyCharsIsValid() {
        String fiftyChars = "12345678901234567890123456789012345678901234567890";
        Task task = new Task("123", "Buy Groceries", fiftyChars);
        assertEquals(fiftyChars, task.getDescription());
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    @Test
    void testSetNameUpdatesSuccessfully() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        task.setName("Return Items");
        assertEquals("Return Items", task.getName());
    }

    @Test
    void testSetNameNullThrows() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        assertThrows(InvalidTaskDataException.class, () -> task.setName(null));
    }

    @Test
    void testSetNameTooLongThrows() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        assertThrows(InvalidTaskDataException.class, () ->
            task.setName("123456789012345678901"));
    }

    @Test
    void testSetDescriptionUpdatesSuccessfully() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        task.setDescription("Grab items from aisle 5 and checkout lane 2");
        assertEquals("Grab items from aisle 5 and checkout lane 2", task.getDescription());
    }

    @Test
    void testSetDescriptionNullThrows() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        assertThrows(InvalidTaskDataException.class, () -> task.setDescription(null));
    }

    @Test
    void testSetDescriptionTooLongThrows() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        assertThrows(InvalidTaskDataException.class, () ->
            task.setDescription("123456789012345678901234567890123456789012345678901"));
    }

    // ── Task ID is not updatable ──────────────────────────────────────────────

    @Test
    void testTaskIdIsImmutable() {
        Task task = new Task("123", "Buy Groceries", "Pick up milk, eggs, and bread");
        // There is no setTaskId method; confirm the ID never changes
        assertEquals("123", task.getTaskId());
    }
}