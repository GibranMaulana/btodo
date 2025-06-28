package com.example.persistence;

import com.example.model.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class JsonConfigTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = JsonConfig.newMapper();
    }

    @Test
    @DisplayName("Should create ObjectMapper with proper configuration")
    void testMapperCreation() {
        // Act & Assert
        assertNotNull(mapper);
        assertTrue(mapper.getRegisteredModuleIds().contains("jackson-datatype-jsr310"));
    }

    @Test
    @DisplayName("Should serialize and deserialize LocalDateTime correctly")
    void testLocalDateTimeSerialization() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task();
        task.setTitle("Test Task");
        task.setCreatedAt(now);
        task.setDueDate(now.plusDays(1));

        // Act - Serialize
        String json = mapper.writeValueAsString(task);
        
        // Assert - JSON should contain the datetime
        assertNotNull(json);
        assertTrue(json.length() > 0);
        
        // Act - Deserialize
        Task deserializedTask = mapper.readValue(json, Task.class);
        
        // Assert - Dates should be preserved
        assertEquals(task.getTitle(), deserializedTask.getTitle());
        assertEquals(task.getCreatedAt(), deserializedTask.getCreatedAt());
        assertEquals(task.getDueDate(), deserializedTask.getDueDate());
    }

    @Test
    @DisplayName("Should serialize and deserialize Task list correctly")
    void testTaskListSerialization() throws Exception {
        // Arrange
        LinkedList<Task> tasks = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();
        
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("First task");
        task1.setCreatedAt(now);
        task1.setPriority(1);
        
        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Second task");
        task2.setCreatedAt(now.plusHours(1));
        task2.setPriority(2);
        
        tasks.add(task1);
        tasks.add(task2);

        // Act - Serialize
        String json = mapper.writeValueAsString(tasks);
        
        // Assert - JSON should be valid
        assertNotNull(json);
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        
        // Act - Deserialize
        LinkedList<Task> deserializedTasks = mapper.readValue(json, 
            new TypeReference<LinkedList<Task>>() {});
        
        // Assert - Tasks should be preserved
        assertNotNull(deserializedTasks);
        assertEquals(2, deserializedTasks.size());
        
        assertEquals(task1.getTitle(), deserializedTasks.get(0).getTitle());
        assertEquals(task1.getDescription(), deserializedTasks.get(0).getDescription());
        assertEquals(task1.getPriority(), deserializedTasks.get(0).getPriority());
        
        assertEquals(task2.getTitle(), deserializedTasks.get(1).getTitle());
        assertEquals(task2.getDescription(), deserializedTasks.get(1).getDescription());
        assertEquals(task2.getPriority(), deserializedTasks.get(1).getPriority());
    }

    @Test
    @DisplayName("Should handle null values in Task fields")
    void testNullValueHandling() throws Exception {
        // Arrange
        Task task = new Task();
        task.setTitle("Task with nulls");
        // Leave other fields as null

        // Act - Serialize
        String json = mapper.writeValueAsString(task);
        
        // Act - Deserialize
        Task deserializedTask = mapper.readValue(json, Task.class);
        
        // Assert
        assertEquals(task.getTitle(), deserializedTask.getTitle());
        assertNull(deserializedTask.getDescription());
        assertNull(deserializedTask.getCreatedAt());
        assertNull(deserializedTask.getDueDate());
    }

    @Test
    @DisplayName("Should handle empty task list")
    void testEmptyTaskListSerialization() throws Exception {
        // Arrange
        LinkedList<Task> emptyTasks = new LinkedList<>();

        // Act - Serialize
        String json = mapper.writeValueAsString(emptyTasks);
        
        // Assert - Should be empty JSON array
        assertEquals("[]", json);
        
        // Act - Deserialize
        LinkedList<Task> deserializedTasks = mapper.readValue(json, 
            new TypeReference<LinkedList<Task>>() {});
        
        // Assert
        assertNotNull(deserializedTasks);
        assertTrue(deserializedTasks.isEmpty());
    }

    @Test
    @DisplayName("Should create new mapper instances")
    void testNewMapperInstances() {
        // Act
        ObjectMapper mapper1 = JsonConfig.newMapper();
        ObjectMapper mapper2 = JsonConfig.newMapper();
        
        // Assert - Should be different instances but with same configuration
        assertNotSame(mapper1, mapper2);
        assertEquals(mapper1.getRegisteredModuleIds(), mapper2.getRegisteredModuleIds());
    }

    @Test
    @DisplayName("Should handle Task with complex tags structure")
    void testComplexTagsSerialization() throws Exception {
        // Arrange
        Task task = new Task();
        task.setTitle("Task with complex tags");
        
        LinkedList<String> tags = new LinkedList<>();
        tags.add("work");
        tags.add("urgent");
        tags.add("project-alpha");
        tags.add("tag with spaces");
        tags.add("tag-with-special-chars!@#");
        task.setTags(tags);

        // Act - Serialize
        String json = mapper.writeValueAsString(task);
        
        // Act - Deserialize
        Task deserializedTask = mapper.readValue(json, Task.class);
        
        // Assert
        assertNotNull(deserializedTask.getTags());
        assertEquals(tags.size(), deserializedTask.getTags().size());
        assertTrue(deserializedTask.getTags().containsAll(tags));
    }
}
