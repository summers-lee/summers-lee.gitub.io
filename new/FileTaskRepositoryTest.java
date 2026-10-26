import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

/** Tests the persistence-backed repository, including survival across "restarts". */
public class FileTaskRepositoryTest {

    @TempDir
    Path tempDir;

    private Path dataFile() {
        return tempDir.resolve("tasks.txt");
    }

    @Test
    void testStartsEmptyWhenFileDoesNotExist() {
        FileTaskRepository repo = new FileTaskRepository(dataFile());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void testTaskSurvivesRestart() {
        new TaskService(new FileTaskRepository(dataFile()))
            .addTask(new Task("1", "Buy Groceries", "Pick up milk, eggs, and bread"));

        // A brand-new repository over the same file simulates restarting the program.
        TaskService restarted = new TaskService(new FileTaskRepository(dataFile()));
        assertEquals("Buy Groceries", restarted.getTask("1").getName());
        assertEquals("Pick up milk, eggs, and bread", restarted.getTask("1").getDescription());
    }

    @Test
    void testUpdateSurvivesRestart() {
        TaskService service = new TaskService(new FileTaskRepository(dataFile()));
        service.addTask(new Task("1", "Buy Groceries", "Pick up milk"));
        service.updateName("1", "Return Items");
        service.updateDescription("1", "Aisle 5");

        TaskService restarted = new TaskService(new FileTaskRepository(dataFile()));
        assertEquals("Return Items", restarted.getTask("1").getName());
        assertEquals("Aisle 5", restarted.getTask("1").getDescription());
    }

    @Test
    void testDeleteSurvivesRestart() {
        TaskService service = new TaskService(new FileTaskRepository(dataFile()));
        service.addTask(new Task("1", "One", "First"));
        service.addTask(new Task("2", "Two", "Second"));
        service.deleteTask("1");

        TaskService restarted = new TaskService(new FileTaskRepository(dataFile()));
        assertThrows(TaskNotFoundException.class, () -> restarted.getTask("1"));
        assertEquals("Two", restarted.getTask("2").getName());
    }

    @Test
    void testDuplicateIdAcrossRestartThrows() {
        new TaskService(new FileTaskRepository(dataFile()))
            .addTask(new Task("1", "One", "First"));

        TaskService restarted = new TaskService(new FileTaskRepository(dataFile()));
        assertThrows(DuplicateTaskException.class,
            () -> restarted.addTask(new Task("1", "Other", "Same ID")));
    }

    @Test
    void testSpecialCharactersRoundTrip() {
        // tab, backslash and newline inside fields must not corrupt the file format
        String name = "a\tb\\c";
        String description = "line1\nline2, with comma";
        new TaskService(new FileTaskRepository(dataFile()))
            .addTask(new Task("1", name, description));

        Task loaded = new TaskService(new FileTaskRepository(dataFile())).getTask("1");
        assertEquals(name, loaded.getName());
        assertEquals(description, loaded.getDescription());
    }

    @Test
    void testFindMissingTaskThrows() {
        FileTaskRepository repo = new FileTaskRepository(dataFile());
        assertThrows(TaskNotFoundException.class, () -> repo.findById("nope"));
    }

    @Test
    void testCorruptedFileThrowsStorageException() throws IOException {
        Files.write(dataFile(), "this line has no tabs\n".getBytes(StandardCharsets.UTF_8));
        assertThrows(TaskStorageException.class, () -> new FileTaskRepository(dataFile()));
    }

    @Test
    void testFileWithInvalidTaskDataThrowsStorageException() throws IOException {
        // name is 21 characters, over the 20-character limit
        String badLine = "1\t123456789012345678901\tdesc\n";
        Files.write(dataFile(), badLine.getBytes(StandardCharsets.UTF_8));
        assertThrows(TaskStorageException.class, () -> new FileTaskRepository(dataFile()));
    }
}
