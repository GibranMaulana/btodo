# Task Persistence Testing

This directory contains comprehensive tests for the task persistence functionality in the btodo application.

## Test Structure

### 1. TaskRepositoryTest
- **Purpose**: Unit tests for the `TaskRepository` class
- **Coverage**: 
  - Saving and loading tasks (empty lists, single tasks, multiple tasks)
  - Error handling (non-existent files, corrupted JSON)
  - Edge cases (null fields, special characters, large datasets)
  - Performance testing

### 2. JsonConfigTest  
- **Purpose**: Unit tests for the `JsonConfig` class
- **Coverage**:
  - ObjectMapper configuration and creation
  - JSON serialization/deserialization of Task objects
  - LocalDateTime handling (JSR-310 support)
  - Complex data structures (tags, nested objects)

### 3. PersistenceIntegrationTest
- **Purpose**: Integration tests for the complete persistence workflow
- **Coverage**:
  - End-to-end task lifecycle (create → save → load → modify → delete)
  - File system interactions
  - Error recovery scenarios
  - Performance with large datasets

## Test Data

The tests use temporary files and directories to avoid interfering with the main application data. Each test class uses:
- `@TempDir` for isolated test environments
- Custom `TestTaskRepository` implementations that override file paths
- Sample task data generation for consistent testing

## Key Test Scenarios

### Data Integrity
- ✅ Round-trip serialization preserves all task fields
- ✅ Date/time values are correctly handled
- ✅ Complex tag structures are maintained
- ✅ Order preservation in task lists

### Error Handling
- ✅ Graceful recovery from missing files
- ✅ Handling of corrupted JSON data
- ✅ Proper error reporting and logging

### Performance
- ✅ Large dataset handling (5000+ tasks)
- ✅ Reasonable save/load times
- ✅ Memory efficiency

### Edge Cases
- ✅ Empty task lists
- ✅ Tasks with null/undefined fields
- ✅ Special characters in task content
- ✅ Concurrent access scenarios

## Running the Tests

To run all persistence tests:
```bash
mvn test -Dtest="com.example.persistence.*"
```

To run specific test classes:
```bash
# Unit tests only
mvn test -Dtest="TaskRepositoryTest,JsonConfigTest"

# Integration tests only  
mvn test -Dtest="PersistenceIntegrationTest"
```

## Test Coverage Goals

The persistence tests aim to achieve:
- **Line Coverage**: >95% of persistence layer code
- **Branch Coverage**: >90% of conditional logic paths
- **Method Coverage**: 100% of public methods

## Future Enhancements

Potential additional test scenarios:
- Concurrent access testing with multiple threads
- Database migration testing (if moving from JSON to database)
- Backup and restore functionality testing
- Cross-platform file system compatibility testing
