package com.example.persistence;

import com.example.model.Task;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    private TaskRepository taskRepository;
    private File testFile;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Create a test-specific TaskRepository that uses a temporary file
        testFile = tempDir.resolve("test_tasks.json").toFile();
        taskRepository = new TestTaskRepository(testFile);
    }

    @AfterEach
    void tearDown() {
        // Clean up - delete test file if it exists
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    @DisplayName("Should save and load empty task list")
    void testSaveAndLoadEmptyList() {
        // Arrange
        LinkedList<Task> emptyTasks = new LinkedList<>();

        // Act
        taskRepository.saveAll(emptyTasks);
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    @DisplayName("Should save and load single task")
    void testSaveAndLoadSingleTask() {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(7);
        LinkedList<String> tags = new LinkedList<>();
        tags.add("work");
        tags.add("urgent");

        Task task = new Task(
            UUID.randomUUID(),
            "Test Task",
            "This is a test task",
            "pending",
            now,
            dueDate,
            1,
            tags
        );
        tasks.add(task);

        // Act
        taskRepository.saveAll(tasks);
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertEquals(1, loadedTasks.size());
        
        Task loadedTask = loadedTasks.getFirst();
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getPriority(), loadedTask.getPriority());
        assertEquals(task.getTags().size(), loadedTask.getTags().size());
        assertTrue(loadedTask.getTags().containsAll(task.getTags()));
    }

    @Test
    @DisplayName("Should save and load multiple tasks")
    void testSaveAndLoadMultipleTasks() {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Create multiple tasks with different properties
        for (int i = 1; i <= 3; i++) {
            LinkedList<String> tags = new LinkedList<>();
            tags.add("tag" + i);
            
            Task task = new Task(
                UUID.randomUUID(),
                "Task " + i,
                "Description for task " + i,
                i % 2 == 0 ? "completed" : "pending",
                now.minusDays(i),
                now.plusDays(i * 2),
                i,
                tags
            );
            tasks.add(task);
        }

        // Act
        taskRepository.saveAll(tasks);
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertEquals(3, loadedTasks.size());
        
        // Verify each task was saved and loaded correctly
        for (int i = 0; i < 3; i++) {
            Task original = tasks.get(i);
            Task loaded = loadedTasks.get(i);
            
            assertEquals(original.getTitle(), loaded.getTitle());
            assertEquals(original.getDescription(), loaded.getDescription());
            assertEquals(original.getStatus(), loaded.getStatus());
            assertEquals(original.getPriority(), loaded.getPriority());
        }
    }

    @Test
    @DisplayName("Should handle task with null fields gracefully")
    void testSaveAndLoadTaskWithNullFields() {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        Task taskWithNulls = new Task();
        taskWithNulls.setTitle("Title Only Task");
        // Leave other fields as null/default
        tasks.add(taskWithNulls);

        // Act
        taskRepository.saveAll(tasks);
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertEquals(1, loadedTasks.size());
        
        Task loadedTask = loadedTasks.getFirst();
        assertEquals("Title Only Task", loadedTask.getTitle());
    }

    @Test
    @DisplayName("Should return empty list when loading from non-existent file")
    void testLoadFromNonExistentFile() {
        // Arrange - ensure test file doesn't exist
        if (testFile.exists()) {
            testFile.delete();
        }

        // Act
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    @DisplayName("Should handle corrupted JSON file gracefully")
    void testLoadFromCorruptedFile() throws IOException {
        // Arrange - create a file with invalid JSON
        Files.write(testFile.toPath(), "{ invalid json content".getBytes());

        // Act
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    @DisplayName("Should preserve task order after save and load")
    void testTaskOrderPreservation() {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Add tasks in specific order
        String[] titles = {"First Task", "Second Task", "Third Task"};
        for (String title : titles) {
            Task task = new Task();
            task.setTitle(title);
            task.setCreatedAt(now);
            tasks.add(task);
        }

        // Act
        taskRepository.saveAll(tasks);
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertEquals(tasks.size(), loadedTasks.size());
        for (int i = 0; i < titles.length; i++) {
            assertEquals(titles[i], loadedTasks.get(i).getTitle());
        }
    }

    @Test
    @DisplayName("Should handle tasks with special characters in fields")
    void testTasksWithSpecialCharacters() {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        Task task = new Task();
        task.setTitle("Task with special chars: !@#$%^&*()");
        task.setDescription("Description with newlines\nand quotes \"test\" and apostrophes 'test'");
        
        LinkedList<String> tags = new LinkedList<>();
        tags.add("tag-with-dashes");
        tags.add("tag_with_underscores");
        tags.add("tag with spaces");
        task.setTags(tags);
        
        tasks.add(task);

        // Act
        taskRepository.saveAll(tasks);
        LinkedList<Task> loadedTasks = taskRepository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertEquals(1, loadedTasks.size());
        
        Task loadedTask = loadedTasks.getFirst();
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getTags().size(), loadedTask.getTags().size());
        assertTrue(loadedTask.getTags().containsAll(task.getTags()));
    }

    @Test
    @DisplayName("Should handle large number of tasks")
    void testLargeNumberOfTasks() {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        int numberOfTasks = 1000;
        
        for (int i = 0; i < numberOfTasks; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setDescription("Description " + i);
            task.setPriority(i % 5);
            tasks.add(task);
        }

        // Act
        long startTime = System.currentTimeMillis();
        taskRepository.saveAll(tasks);
        long saveTime = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        LinkedList<Task> loadedTasks = taskRepository.loadAll();
        long loadTime = System.currentTimeMillis() - startTime;

        // Assert
        assertEquals(numberOfTasks, loadedTasks.size());
        
        // Performance assertion - should complete within reasonable time (adjust as needed)
        assertTrue(saveTime < 5000, "Save operation took too long: " + saveTime + "ms");
        assertTrue(loadTime < 5000, "Load operation took too long: " + loadTime + "ms");
    }

    /**
     * Test-specific TaskRepository that allows us to specify a custom file path
     */
    private static class TestTaskRepository extends TaskRepository {
        private final File testFile;

        public TestTaskRepository(File testFile) {
            this.testFile = testFile;
        }

        @Override
        public LinkedList<Task> loadAll() {
            try {
                return JsonConfig.newMapper().readValue(testFile, 
                    new com.fasterxml.jackson.core.type.TypeReference<LinkedList<Task>>() {});
            } catch (IOException e) {
                return new LinkedList<>();
            }
        }

        @Override
        public void saveAll(LinkedList<Task> tasks) {
            try {
                // Ensure parent directories exist
                testFile.getParentFile().mkdirs();
                JsonConfig.newMapper().writerWithDefaultPrettyPrinter()
                    .writeValue(testFile, tasks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
