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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersistenceIntegrationTest {

    @TempDir
    static Path tempDir;
    
    private static File testFile;
    private static TaskRepository repository;

    @BeforeAll
    static void setUpClass() {
        testFile = tempDir.resolve("integration_test_tasks.json").toFile();
        repository = new TestTaskRepository(testFile);
    }

    @Test
    @Order(1)
    @DisplayName("Integration Test: Create and save initial tasks")
    void testCreateAndSaveInitialTasks() {
        // Arrange
        LinkedList<Task> tasks = createSampleTasks();

        // Act
        repository.saveAll(tasks);

        // Assert
        assertTrue(testFile.exists());
        assertTrue(testFile.length() > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test: Load previously saved tasks")
    void testLoadPreviouslySavedTasks() {
        // Act
        LinkedList<Task> loadedTasks = repository.loadAll();

        // Assert
        assertNotNull(loadedTasks);
        assertEquals(3, loadedTasks.size());
        
        // Verify specific task details
        Task firstTask = loadedTasks.get(0);
        assertEquals("Complete project documentation", firstTask.getTitle());
        assertEquals("pending", firstTask.getStatus());
        assertEquals(1, firstTask.getPriority());
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test: Modify and save updated tasks")
    void testModifyAndSaveUpdatedTasks() {
        // Arrange
        LinkedList<Task> tasks = repository.loadAll();
        
        // Modify first task
        Task firstTask = tasks.get(0);
        firstTask.setStatus("completed");
        firstTask.setDescription("Updated: " + firstTask.getDescription());
        
        // Add a new task
        Task newTask = new Task();
        newTask.setTitle("New urgent task");
        newTask.setDescription("This task was added later");
        newTask.setStatus("pending");
        newTask.setPriority(0); // Highest priority
        newTask.setCreatedAt(LocalDateTime.now());
        
        LinkedList<String> urgentTags = new LinkedList<>();
        urgentTags.add("urgent");
        urgentTags.add("new");
        newTask.setTags(urgentTags);
        
        tasks.add(newTask);

        // Act
        repository.saveAll(tasks);
        LinkedList<Task> reloadedTasks = repository.loadAll();

        // Assert
        assertEquals(4, reloadedTasks.size());
        
        // Verify modifications
        Task modifiedTask = reloadedTasks.get(0);
        assertEquals("completed", modifiedTask.getStatus());
        assertTrue(modifiedTask.getDescription().startsWith("Updated:"));
        
        // Verify new task
        Task addedTask = reloadedTasks.get(3);
        assertEquals("New urgent task", addedTask.getTitle());
        assertEquals(0, addedTask.getPriority());
        assertTrue(addedTask.getTags().contains("urgent"));
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test: Remove tasks and save")
    void testRemoveTasksAndSave() {
        // Arrange
        LinkedList<Task> tasks = repository.loadAll();
        int originalSize = tasks.size();
        
        // Remove completed tasks
        tasks.removeIf(task -> "completed".equals(task.getStatus()));

        // Act
        repository.saveAll(tasks);
        LinkedList<Task> reloadedTasks = repository.loadAll();

        // Assert
        assertTrue(reloadedTasks.size() < originalSize);
        
        // Verify no completed tasks remain
        boolean hasCompletedTasks = reloadedTasks.stream()
            .anyMatch(task -> "completed".equals(task.getStatus()));
        assertFalse(hasCompletedTasks);
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test: Clear all tasks")
    void testClearAllTasks() {
        // Arrange
        LinkedList<Task> emptyTasks = new LinkedList<>();

        // Act
        repository.saveAll(emptyTasks);
        LinkedList<Task> reloadedTasks = repository.loadAll();

        // Assert
        assertTrue(reloadedTasks.isEmpty());
        assertTrue(testFile.exists()); // File should still exist but be empty array
    }

    @Test
    @Order(6)
    @DisplayName("Integration Test: Performance with large dataset")
    void testPerformanceWithLargeDataset() {
        // Arrange
        LinkedList<Task> largeTasks = new LinkedList<>();
        int taskCount = 5000;
        
        for (int i = 0; i < taskCount; i++) {
            Task task = new Task();
            task.setTitle("Performance Test Task " + i);
            task.setDescription("Description for task " + i);
            task.setStatus(i % 3 == 0 ? "completed" : "pending");
            task.setPriority(i % 5);
            task.setCreatedAt(LocalDateTime.now().minusDays(i % 30));
            
            LinkedList<String> tags = new LinkedList<>();
            tags.add("performance");
            tags.add("batch-" + (i / 100));
            task.setTags(tags);
            
            largeTasks.add(task);
        }

        // Act - Measure save performance
        long saveStart = System.currentTimeMillis();
        repository.saveAll(largeTasks);
        long saveTime = System.currentTimeMillis() - saveStart;

        // Act - Measure load performance
        long loadStart = System.currentTimeMillis();
        LinkedList<Task> loadedTasks = repository.loadAll();
        long loadTime = System.currentTimeMillis() - loadStart;

        // Assert
        assertEquals(taskCount, loadedTasks.size());
        
        // Performance assertions (adjust thresholds as needed)
        assertTrue(saveTime < 10000, "Save operation took too long: " + saveTime + "ms");
        assertTrue(loadTime < 10000, "Load operation took too long: " + loadTime + "ms");
        
        System.out.println("Performance results:");
        System.out.println("Save time for " + taskCount + " tasks: " + saveTime + "ms");
        System.out.println("Load time for " + taskCount + " tasks: " + loadTime + "ms");
        System.out.println("File size: " + testFile.length() + " bytes");
    }

    @Test
    @Order(7)
    @DisplayName("Integration Test: File corruption recovery")
    void testFileCorruptionRecovery() throws IOException {
        // Arrange - Corrupt the file
        Files.write(testFile.toPath(), "{ corrupted json content".getBytes());

        // Act
        LinkedList<Task> recoveredTasks = repository.loadAll();

        // Assert - Should recover gracefully with empty list
        assertNotNull(recoveredTasks);
        assertTrue(recoveredTasks.isEmpty());

        // Verify we can save new data after corruption
        LinkedList<Task> newTasks = createSampleTasks();
        repository.saveAll(newTasks);
        
        LinkedList<Task> reloadedTasks = repository.loadAll();
        assertEquals(3, reloadedTasks.size());
    }

    /**
     * Helper method to create sample tasks for testing
     */
    private static LinkedList<Task> createSampleTasks() {
        LinkedList<Task> tasks = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();

        // Task 1
        Task task1 = new Task();
        task1.setTitle("Complete project documentation");
        task1.setDescription("Write comprehensive documentation for the project");
        task1.setStatus("pending");
        task1.setPriority(1);
        task1.setCreatedAt(now.minusDays(3));
        task1.setDueDate(now.plusDays(7));
        
        LinkedList<String> tags1 = new LinkedList<>();
        tags1.add("documentation");
        tags1.add("project");
        task1.setTags(tags1);
        
        // Task 2
        Task task2 = new Task();
        task2.setTitle("Fix critical bug in payment system");
        task2.setDescription("Address the payment processing issue reported by users");
        task2.setStatus("in-progress");
        task2.setPriority(0); // Highest priority
        task2.setCreatedAt(now.minusDays(1));
        task2.setDueDate(now.plusDays(2));
        
        LinkedList<String> tags2 = new LinkedList<>();
        tags2.add("bug");
        tags2.add("critical");
        tags2.add("payment");
        task2.setTags(tags2);
        
        // Task 3
        Task task3 = new Task();
        task3.setTitle("Code review for new feature");
        task3.setDescription("Review the user authentication feature implementation");
        task3.setStatus("pending");
        task3.setPriority(2);
        task3.setCreatedAt(now.minusHours(6));
        task3.setDueDate(now.plusDays(5));
        
        LinkedList<String> tags3 = new LinkedList<>();
        tags3.add("review");
        tags3.add("authentication");
        task3.setTags(tags3);

        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        
        return tasks;
    }

    /**
     * Test-specific TaskRepository for integration testing
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
                testFile.getParentFile().mkdirs();
                JsonConfig.newMapper().writerWithDefaultPrettyPrinter()
                    .writeValue(testFile, tasks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
